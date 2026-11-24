package com.bluemoon.service.impl;

import com.bluemoon.dto.household.CreateHouseholdRequest;
import com.bluemoon.dto.household.HouseholdDto;
import com.bluemoon.entity.Apartment;
import com.bluemoon.entity.Household;
import com.bluemoon.entity.HouseholdApartment;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.ApartmentRepository;
import com.bluemoon.repository.HouseholdApartmentRepository;
import com.bluemoon.repository.HouseholdPersonRepository;
import com.bluemoon.repository.HouseholdRepository;
import com.bluemoon.service.HouseholdService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation cho Quản lý hộ gia đình (Thành viên 2 - Thành - F03).
 */
@Service
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdApartmentRepository householdApartmentRepository;
    private final HouseholdPersonRepository householdPersonRepository;
    private final ApartmentRepository apartmentRepository;

    public HouseholdServiceImpl(HouseholdRepository householdRepository,
                                HouseholdApartmentRepository householdApartmentRepository,
                                HouseholdPersonRepository householdPersonRepository,
                                ApartmentRepository apartmentRepository) {
        this.householdRepository = householdRepository;
        this.householdApartmentRepository = householdApartmentRepository;
        this.householdPersonRepository = householdPersonRepository;
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HouseholdDto> findAll(String keyword) {
        // TODO [Thành - Week 1]: Hoàn thiện bộ lọc keyword theo tên hộ hoặc số điện thoại
        return householdRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HouseholdDto findById(Long id) {
        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new AppException("HOUSEHOLD_NOT_FOUND", "Không tìm thấy hộ gia đình", HttpStatus.NOT_FOUND));
        return mapToDto(household);
    }

    @Override
    @Transactional
    public HouseholdDto create(CreateHouseholdRequest request) {
        // TODO [Thành - Week 1]: Kiểm tra căn hộ đang hoạt động và chưa có hộ nào gắn kết còn hiệu lực
        Apartment apartment = apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ", HttpStatus.NOT_FOUND));

        Household household = new Household(request.getHouseholdName(), request.getContactPhone());
        Household saved = householdRepository.save(household);

        // Gán căn hộ
        HouseholdApartment ha = new HouseholdApartment();
        ha.setHousehold(saved);
        ha.setApartment(apartment);
        ha.setStartDate(LocalDate.now());
        householdApartmentRepository.save(ha);

        return mapToDto(saved);
    }

    private HouseholdDto mapToDto(Household household) {
        HouseholdDto dto = new HouseholdDto();
        dto.setHouseholdId(household.getHouseholdId());
        dto.setHouseholdName(household.getHouseholdName());
        dto.setContactPhone(household.getContactPhone());
        dto.setIsActive(household.getIsActive());
        dto.setCreatedAt(household.getCreatedAt());

        householdApartmentRepository.findActiveByHouseholdId(household.getHouseholdId())
                .ifPresent(ha -> dto.setApartmentNumber(ha.getApartment().getApartmentNumber()));

        int count = householdPersonRepository.findByHousehold_HouseholdId(household.getHouseholdId()).size();
        dto.setMemberCount(count);

        return dto;
    }
}
