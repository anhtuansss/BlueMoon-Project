package com.bluemoon.service.impl;

import com.bluemoon.dto.response.BillableApartmentSnapshot;
import com.bluemoon.dto.fee.*;
import com.bluemoon.entity.CollectionCampaign;
import com.bluemoon.entity.FeeObligation;
import com.bluemoon.entity.FeeType;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.ApartmentRepository;
import com.bluemoon.repository.CollectionCampaignRepository;
import com.bluemoon.repository.FeeObligationRepository;
import com.bluemoon.repository.FeeTypeRepository;
import com.bluemoon.service.ApartmentQueryService;
import com.bluemoon.service.FeeCampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation cho Quản lý khoản thu và đóng góp (Thành viên 3 - Long - F05).
 */
@Service
public class FeeCampaignServiceImpl implements FeeCampaignService {

    private final FeeTypeRepository feeTypeRepository;
    private final CollectionCampaignRepository collectionCampaignRepository;
    private final FeeObligationRepository feeObligationRepository;
    private final ApartmentQueryService apartmentQueryService;
    private final ApartmentRepository apartmentRepository;

    public FeeCampaignServiceImpl(FeeTypeRepository feeTypeRepository,
                                  CollectionCampaignRepository collectionCampaignRepository,
                                  FeeObligationRepository feeObligationRepository,
                                  ApartmentQueryService apartmentQueryService,
                                  ApartmentRepository apartmentRepository) {
        this.feeTypeRepository = feeTypeRepository;
        this.collectionCampaignRepository = collectionCampaignRepository;
        this.feeObligationRepository = feeObligationRepository;
        this.apartmentQueryService = apartmentQueryService;
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeTypeDto> getActiveFeeTypes() {
        return feeTypeRepository.findByIsActiveTrue().stream()
                .map(this::mapFeeTypeToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeeCampaignPreviewResponse previewCampaign(FeeCampaignPreviewRequest request) {
        // TODO [Long - Week 1]: Hoàn thiện xác thực nghiệp vụ nâng cao cho preview
        FeeType feeType = feeTypeRepository.findById(request.getFeeTypeId())
                .orElseThrow(() -> new AppException("FEE_TYPE_NOT_FOUND", "Không tìm thấy loại khoản thu", HttpStatus.NOT_FOUND));

        if (feeType.getCategory() == FeeType.FeeCategory.DONATION) {
            throw new AppException("INVALID_OPERATION", "Khoản thu tự nguyện không tạo nghĩa vụ phí bắt buộc", HttpStatus.BAD_REQUEST);
        }

        BigDecimal unitPrice = request.getCustomUnitPrice() != null ? request.getCustomUnitPrice() : feeType.getDefaultUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException("INVALID_UNIT_PRICE", "Đơn giá thu phí không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        List<BillableApartmentSnapshot> targetApartments = resolveTargetApartments(request.getTarget());
        List<FeeObligationPreviewItemDto> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BillableApartmentSnapshot apt : targetApartments) {
            // Nghiệp vụ: amount_due = ROUND(areaSnapshot * appliedUnitPrice, 0)
            BigDecimal area = apt.getArea() != null ? apt.getArea() : BigDecimal.ZERO;
            BigDecimal amountDue = area.multiply(unitPrice).setScale(0, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(amountDue);

            items.add(new FeeObligationPreviewItemDto(
                    Long.parseLong(apt.getApartmentId()),
                    apt.getApartmentNumber(),
                    area,
                    unitPrice,
                    amountDue
            ));
        }

        return new FeeCampaignPreviewResponse(unitPrice, items.size(), totalAmount, items);
    }

    @Override
    @Transactional
    public FeeCampaignResponse createCampaign(CreateFeeCampaignRequest request, Long currentAccountId) {
        FeeType feeType = feeTypeRepository.findById(request.getFeeTypeId())
                .orElseThrow(() -> new AppException("FEE_TYPE_NOT_FOUND", "Không tìm thấy loại khoản thu", HttpStatus.NOT_FOUND));

        if (feeType.getCategory() == FeeType.FeeCategory.DONATION) {
            throw new AppException("INVALID_OPERATION", "Khoản thu tự nguyện không tạo nghĩa vụ phí bắt buộc", HttpStatus.BAD_REQUEST);
        }

        // Ràng buộc schema: Không cho phép trùng fee_type_id và fee_period
        if (request.getFeePeriod() != null) {
            collectionCampaignRepository.findByFeeTypeIdAndCyclePeriod(feeType.getId(), request.getFeePeriod())
                    .ifPresent(existing -> {
                        throw new AppException("DUPLICATE_FEE_CAMPAIGN", "Đợt thu cho loại phí và kỳ này đã tồn tại", HttpStatus.CONFLICT);
                    });
        }

        BigDecimal unitPrice = request.getCustomUnitPrice() != null ? request.getCustomUnitPrice() : feeType.getDefaultUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException("INVALID_UNIT_PRICE", "Đơn giá thu phí không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        CollectionCampaign campaign = new CollectionCampaign();
        campaign.setFeeTypeId(feeType.getId());
        campaign.setTitle(request.getCampaignName());
        campaign.setCyclePeriod(request.getFeePeriod());
        campaign.setAppliedUnitPrice(unitPrice);
        campaign.setStartDate(request.getCollectionStartDate());
        campaign.setDueDate(request.getDueDate());
        campaign.setStatus(CollectionCampaign.CampaignStatus.OPEN);
        campaign.setNotes(request.getDescription());
        campaign.setCreatedByAccountId(currentAccountId != null ? currentAccountId : 1L);

        CollectionCampaign savedCampaign = collectionCampaignRepository.save(campaign);

        // Tạo toàn bộ nghĩa vụ phí trong transaction
        List<BillableApartmentSnapshot> targetApartments = resolveTargetApartments(request.getTarget());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<FeeObligation> obligations = new ArrayList<>();

        for (BillableApartmentSnapshot apt : targetApartments) {
            BigDecimal area = apt.getArea() != null ? apt.getArea() : BigDecimal.ZERO;
            BigDecimal amountDue = area.multiply(unitPrice).setScale(0, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(amountDue);

            FeeObligation obligation = new FeeObligation();
            obligation.setCampaignId(savedCampaign.getId());
            obligation.setApartmentId(Long.parseLong(apt.getApartmentId()));
            obligation.setFeeTypeId(feeType.getId());
            obligation.setAreaSnapshot(area);
            obligation.setUnitPriceSnapshot(unitPrice);
            obligation.setAmountDue(amountDue);
            obligation.setStatus(FeeObligation.ObligationStatus.UNPAID);
            obligation.setPaymentStatus(FeeObligation.PaymentStatus.UNPAID);

            obligations.add(obligation);
        }

        feeObligationRepository.saveAll(obligations);

        FeeCampaignResponse response = new FeeCampaignResponse();
        response.setId(savedCampaign.getId());
        response.setFeeTypeId(savedCampaign.getFeeTypeId());
        response.setFeeTypeName(feeType.getName());
        response.setTitle(savedCampaign.getTitle());
        response.setCyclePeriod(savedCampaign.getCyclePeriod());
        response.setAppliedUnitPrice(savedCampaign.getAppliedUnitPrice());
        response.setStartDate(savedCampaign.getStartDate());
        response.setDueDate(savedCampaign.getDueDate());
        response.setStatus(savedCampaign.getStatus().name());
        response.setTotalApartments(obligations.size());
        response.setTotalAmount(totalAmount);
        response.setCreatedAt(savedCampaign.getCreatedAt());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeCampaignResponse> getCampaigns() {
        return collectionCampaignRepository.findAll().stream()
                .map(camp -> {
                    FeeCampaignResponse res = new FeeCampaignResponse();
                    res.setId(camp.getId());
                    res.setFeeTypeId(camp.getFeeTypeId());
                    feeTypeRepository.findById(camp.getFeeTypeId()).ifPresent(ft -> res.setFeeTypeName(ft.getName()));
                    res.setTitle(camp.getTitle());
                    res.setCyclePeriod(camp.getCyclePeriod());
                    res.setAppliedUnitPrice(camp.getAppliedUnitPrice());
                    res.setStartDate(camp.getStartDate());
                    res.setDueDate(camp.getDueDate());
                    res.setStatus(camp.getStatus().name());
                    res.setCreatedAt(camp.getCreatedAt());

                    List<FeeObligation> obligations = feeObligationRepository.findByCampaignId(camp.getId());
                    res.setTotalApartments(obligations.size());
                    res.setTotalAmount(obligations.stream()
                            .map(FeeObligation::getAmountDue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeObligationDto> getObligations(Long campaignId, Long apartmentId) {
        List<FeeObligation> list;
        if (campaignId != null) {
            list = feeObligationRepository.findByCampaignId(campaignId);
        } else if (apartmentId != null) {
            list = feeObligationRepository.findByApartmentId(apartmentId);
        } else {
            list = feeObligationRepository.findAll();
        }

        return list.stream().map(o -> {
            FeeObligationDto dto = new FeeObligationDto();
            dto.setId(o.getId());
            dto.setCampaignId(o.getCampaignId());
            collectionCampaignRepository.findById(o.getCampaignId()).ifPresent(c -> dto.setCampaignTitle(c.getTitle()));
            dto.setApartmentId(o.getApartmentId());
            apartmentRepository.findById(o.getApartmentId()).ifPresent(a -> dto.setApartmentNumber(a.getApartmentNumber()));
            dto.setFeeTypeId(o.getFeeTypeId());
            feeTypeRepository.findById(o.getFeeTypeId()).ifPresent(ft -> dto.setFeeTypeName(ft.getName()));
            dto.setAreaSnapshot(o.getAreaSnapshot());
            dto.setUnitPriceSnapshot(o.getUnitPriceSnapshot());
            dto.setAmountDue(o.getAmountDue());
            dto.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
            dto.setPaymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus().name() : null);
            dto.setPaidAt(o.getPaidAt());
            dto.setCreatedAt(o.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<BillableApartmentSnapshot> resolveTargetApartments(CampaignTargetDto target) {
        if (target != null && "ONE_APARTMENT".equalsIgnoreCase(target.getMode()) && target.getApartmentId() != null) {
            BillableApartmentSnapshot single = apartmentQueryService.getActiveApartment(String.valueOf(target.getApartmentId()));
            return List.of(single);
        }
        return apartmentQueryService.findAllActiveApartments();
    }

    private FeeTypeDto mapFeeTypeToDto(FeeType ft) {
        FeeTypeDto dto = new FeeTypeDto();
        dto.setId(ft.getId());
        dto.setCode(ft.getCode());
        dto.setName(ft.getName());
        dto.setCategory(ft.getCategory() != null ? ft.getCategory().name() : null);
        dto.setChargingMethod(ft.getChargingMethod() != null ? ft.getChargingMethod().name() : null);
        dto.setDefaultUnitPrice(ft.getDefaultUnitPrice());
        dto.setIsActive(ft.getIsActive());
        dto.setDescription(ft.getDescription());
        return dto;
    }
}
