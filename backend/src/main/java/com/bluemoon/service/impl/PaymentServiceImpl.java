package com.bluemoon.service.impl;

import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.PaymentResponse;
import com.bluemoon.dto.payment.SepayWebhookPayload;
import com.bluemoon.entity.*;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.*;
import com.bluemoon.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation cho Quản lý thu tiền & thanh toán (Thành viên 4 - Tuấn - F06).
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeObligationRepository feeObligationRepository;
    private final DonationCampaignRepository donationCampaignRepository;
    private final CollectionCampaignRepository collectionCampaignRepository;
    private final ApartmentRepository apartmentRepository;
    private final PersonRepository personRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              FeeObligationRepository feeObligationRepository,
                              DonationCampaignRepository donationCampaignRepository,
                              CollectionCampaignRepository collectionCampaignRepository,
                              ApartmentRepository apartmentRepository,
                              PersonRepository personRepository,
                              PaymentRequestRepository paymentRequestRepository,
                              BankTransactionRepository bankTransactionRepository) {
        this.paymentRepository = paymentRepository;
        this.feeObligationRepository = feeObligationRepository;
        this.donationCampaignRepository = donationCampaignRepository;
        this.collectionCampaignRepository = collectionCampaignRepository;
        this.apartmentRepository = apartmentRepository;
        this.personRepository = personRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Override
    @Transactional
    public PaymentResponse recordManualPayment(Long obligationId, ManualPaymentRequest request, Long recordedByAccountId) {
        FeeObligation obligation = feeObligationRepository.findById(obligationId)
                .orElseThrow(() -> new AppException("OBLIGATION_NOT_FOUND", "Không tìm thấy nghĩa vụ phí", HttpStatus.NOT_FOUND));

        if (obligation.getStatus() == FeeObligation.ObligationStatus.PAID
                || obligation.getPaymentStatus() == FeeObligation.PaymentStatus.PAID) {
            throw new AppException("OBLIGATION_ALREADY_PAID", "Nghĩa vụ phí này đã được thanh toán hoàn tất", HttpStatus.CONFLICT);
        }

        if (obligation.getStatus() == FeeObligation.ObligationStatus.CANCELLED) {
            throw new AppException("OBLIGATION_CANCELLED", "Nghĩa vụ phí đã bị hủy", HttpStatus.BAD_REQUEST);
        }

        // Nghiệp vụ bắt buộc: Thanh toán một lần và phải đúng 100% số tiền
        if (request.getAmount() == null || request.getAmount().compareTo(obligation.getAmountDue()) != 0) {
            throw new AppException("INVALID_PAYMENT_AMOUNT",
                    "Số tiền thanh toán phải bằng đúng số tiền nghĩa vụ (" + obligation.getAmountDue() + ")", HttpStatus.BAD_REQUEST);
        }

        Payment.PaymentMethod method = parsePaymentMethod(request.getMethod());

        LocalDateTime paymentTime = request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now();

        Payment payment = new Payment();
        payment.setObligationId(obligation.getId());
        payment.setApartmentId(obligation.getApartmentId());
        payment.setPayerPersonId(request.getPayerPersonId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(Payment.PaymentStatus.CONFIRMED);
        payment.setPaymentSource(Payment.PaymentSource.MANUAL);
        payment.setPaidAt(paymentTime);
        payment.setRecordedByAccountId(recordedByAccountId != null ? recordedByAccountId : 1L);
        payment.setNotes(request.getNotes());

        Payment savedPayment = paymentRepository.save(payment);

        // Cập nhật nghĩa vụ phí trong cùng transaction
        obligation.setStatus(FeeObligation.ObligationStatus.PAID);
        obligation.setPaymentStatus(FeeObligation.PaymentStatus.PAID);
        obligation.setPaidAt(paymentTime);
        feeObligationRepository.save(obligation);

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse recordManualDonationPayment(Long donationCampaignId, ManualPaymentRequest request, Long recordedByAccountId) {
        DonationCampaign campaign = donationCampaignRepository.findById(donationCampaignId)
                .orElseThrow(() -> new AppException("CAMPAIGN_NOT_FOUND", "Không tìm thấy đợt vận động đóng góp", HttpStatus.NOT_FOUND));

        if (campaign.getStatus() != DonationCampaign.DonationStatus.OPEN) {
            throw new AppException("CAMPAIGN_CLOSED", "Đợt vận động này đã kết thúc, không tiếp nhận đóng góp mới", HttpStatus.BAD_REQUEST);
        }

        if (request.getApartmentId() == null) {
            throw new AppException("APARTMENT_REQUIRED", "Cần chọn căn hộ thực hiện đóng góp", HttpStatus.BAD_REQUEST);
        }

        apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ đóng góp", HttpStatus.NOT_FOUND));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("INVALID_DONATION_AMOUNT", "Số tiền đóng góp phải lớn hơn 0", HttpStatus.BAD_REQUEST);
        }

        Payment.PaymentMethod method = parsePaymentMethod(request.getMethod());

        LocalDateTime paymentTime = request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now();

        Payment payment = new Payment();
        payment.setDonationCampaignId(campaign.getId());
        payment.setObligationId(null);
        payment.setApartmentId(request.getApartmentId());
        payment.setPayerPersonId(request.getPayerPersonId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(Payment.PaymentStatus.CONFIRMED);
        payment.setPaymentSource(Payment.PaymentSource.MANUAL);
        payment.setPaidAt(paymentTime);
        payment.setRecordedByAccountId(recordedByAccountId != null ? recordedByAccountId : 1L);
        payment.setNotes(request.getNotes());

        Payment savedPayment = paymentRepository.save(payment);

        // Cập nhật tổng tiền đã nhận của chiến dịch đóng góp (không sinh FeeObligation)
        BigDecimal currentTotal = campaign.getTotalReceived() != null ? campaign.getTotalReceived() : BigDecimal.ZERO;
        campaign.setTotalReceived(currentTotal.add(request.getAmount()));
        donationCampaignRepository.save(campaign);

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments(Long apartmentId, Long obligationId, Long donationCampaignId) {
        List<Payment> payments;
        if (obligationId != null) {
            payments = paymentRepository.findByObligationId(obligationId);
        } else if (donationCampaignId != null) {
            payments = paymentRepository.findByDonationCampaignId(donationCampaignId);
        } else if (apartmentId != null) {
            payments = paymentRepository.findByApartmentIdOrderByPaidAtDesc(apartmentId);
        } else {
            payments = paymentRepository.findAllByOrderByPaidAtDesc();
        }

        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException("PAYMENT_NOT_FOUND", "Không tìm thấy giao dịch thanh toán", HttpStatus.NOT_FOUND));
        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public void processWebhook(SepayWebhookPayload payload) {
        if (payload == null || payload.getId() == null) {
            return;
        }

        String externalId = String.valueOf(payload.getId());
        if (bankTransactionRepository.existsByExternalTransactionId(externalId)) {
            // Idempotent: Bỏ qua giao dịch đã lưu
            return;
        }

        BankTransaction tx = new BankTransaction();
        tx.setExternalTransactionId(externalId);
        tx.setGateway(BankTransaction.Gateway.SEPAY);
        tx.setAccountNumber(payload.getAccountNumber() != null ? payload.getAccountNumber() : "UNKNOWN");
        tx.setSubAccount(payload.getSubAccount());
        tx.setAmount(payload.getTransferAmount());
        tx.setDirection("in".equalsIgnoreCase(payload.getTransferType()) ? BankTransaction.Direction.IN : BankTransaction.Direction.OUT);
        tx.setContent(payload.getContent() != null ? payload.getContent() : "");
        tx.setReferenceCode(payload.getReferenceCode());
        tx.setProcessingStatus(BankTransaction.ProcessingStatus.RECEIVED);
        tx.setNotes("Nhận từ SePay Webhook");

        bankTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void processMockWebhook(MockSepayWebhookRequest request) {
        if (bankTransactionRepository.existsByExternalTransactionId(request.getTransactionId())) {
            throw new AppException("DUPLICATE_TRANSACTION", "Giao dịch đã tồn tại (idempotency check)", HttpStatus.CONFLICT);
        }

        BankTransaction tx = new BankTransaction();
        tx.setExternalTransactionId(request.getTransactionId());
        tx.setGateway(BankTransaction.Gateway.SEPAY);
        tx.setAccountNumber(request.getAccountNumber() != null ? request.getAccountNumber() : "TEST_ACC");
        tx.setAmount(request.getAmount());
        tx.setDirection(BankTransaction.Direction.IN);
        tx.setContent(request.getContent());
        tx.setReferenceCode(request.getReferenceCode());
        tx.setProcessingStatus(BankTransaction.ProcessingStatus.RECEIVED);
        tx.setNotes("Giao dịch kiểm thử qua Mock Webhook");

        bankTransactionRepository.save(tx);
    }

    private Payment.PaymentMethod parsePaymentMethod(String methodStr) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(methodStr.toUpperCase());
            if (method == Payment.PaymentMethod.QR) {
                throw new IllegalArgumentException();
            }
            return method;
        } catch (Exception e) {
            throw new AppException("INVALID_PAYMENT_METHOD", "Phương thức thanh toán thủ công chỉ chấp nhận CASH hoặc BANK_TRANSFER", HttpStatus.BAD_REQUEST);
        }
    }

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse res = new PaymentResponse();
        res.setId(p.getId());
        res.setObligationId(p.getObligationId());
        res.setDonationCampaignId(p.getDonationCampaignId());
        res.setApartmentId(p.getApartmentId());
        res.setPayerPersonId(p.getPayerPersonId());
        res.setAmount(p.getAmount());
        res.setPaymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null);
        res.setPaymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null);
        res.setPaymentSource(p.getPaymentSource() != null ? p.getPaymentSource().name() : null);
        res.setPaidAt(p.getPaidAt());
        res.setRecordedByAccountId(p.getRecordedByAccountId());
        res.setNotes(p.getNotes());

        // Bổ sung thông tin căn hộ
        if (p.getApartmentId() != null) {
            apartmentRepository.findById(p.getApartmentId())
                    .ifPresent(apt -> res.setApartmentNumber(apt.getApartmentNumber()));
        }

        // Bổ sung tên người nộp
        if (p.getPayerPersonId() != null) {
            personRepository.findById(p.getPayerPersonId())
                    .ifPresent(person -> res.setPayerName(person.getFullName()));
        }

        // Bổ sung tên khoản thu/chiến dịch
        if (p.getObligationId() != null) {
            feeObligationRepository.findById(p.getObligationId()).ifPresent(obl -> {
                collectionCampaignRepository.findById(obl.getCampaignId()).ifPresent(camp -> res.setCampaignTitle(camp.getTitle()));
            });
        } else if (p.getDonationCampaignId() != null) {
            donationCampaignRepository.findById(p.getDonationCampaignId()).ifPresent(don -> res.setCampaignTitle(don.getTitle()));
        }

        return res;
    }
}
