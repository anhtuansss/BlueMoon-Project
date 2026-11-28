package com.bluemoon.service.impl;

import com.bluemoon.dto.payment.*;
import com.bluemoon.entity.*;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.*;
import com.bluemoon.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation cho Quản lý thu tiền, QR và Webhook SePay (Thành viên 4 - Tuấn - F06).
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final FeeObligationRepository feeObligationRepository;
    private final DonationCampaignRepository donationCampaignRepository;
    private final CollectionCampaignRepository collectionCampaignRepository;
    private final ApartmentRepository apartmentRepository;
    private final PersonRepository personRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @Value("${sepay.bank.id:MBBank}")
    private String bankId;

    @Value("${sepay.bank.account-number:202417066}")
    private String bankAccountNumber;

    @Value("${sepay.bank.account-name:BQL CHUNG CU BLUEMOON}")
    private String bankAccountName;

    @Value("${sepay.qr.timeout-minutes:30}")
    private int qrTimeoutMinutes;

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

    // ==========================================
    // 1. THU PHÍ THỦ CÔNG (PHASE 1)
    // ==========================================

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

        obligation.setStatus(FeeObligation.ObligationStatus.PAID);
        obligation.setPaymentStatus(FeeObligation.PaymentStatus.PAID);
        obligation.setPaidAt(paymentTime);
        feeObligationRepository.save(obligation);

        // Đóng các PaymentRequest PENDING cũ nếu có
        paymentRequestRepository.findByObligationIdAndStatus(obligation.getId(), PaymentRequest.RequestStatus.PENDING)
                .ifPresent(pr -> {
                    pr.setStatus(PaymentRequest.RequestStatus.MATCHED);
                    paymentRequestRepository.save(pr);
                });

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

    // ==========================================
    // 2. TẠO & TRA CỨU PAYMENT REQUEST / MÃ QR (PHASE 2)
    // ==========================================

    @Override
    @Transactional
    public PaymentRequestResponse createFeePaymentRequest(Long obligationId, Long accountId) {
        FeeObligation obligation = feeObligationRepository.findById(obligationId)
                .orElseThrow(() -> new AppException("OBLIGATION_NOT_FOUND", "Không tìm thấy nghĩa vụ phí", HttpStatus.NOT_FOUND));

        if (obligation.getStatus() == FeeObligation.ObligationStatus.PAID
                || obligation.getPaymentStatus() == FeeObligation.PaymentStatus.PAID) {
            throw new AppException("OBLIGATION_ALREADY_PAID", "Nghĩa vụ phí này đã được thanh toán", HttpStatus.CONFLICT);
        }

        if (obligation.getStatus() == FeeObligation.ObligationStatus.CANCELLED) {
            throw new AppException("OBLIGATION_CANCELLED", "Nghĩa vụ phí đã bị hủy", HttpStatus.BAD_REQUEST);
        }

        // Ràng buộc duy nhất uq_active_pending_obligation: Chỉ 1 request PENDING còn hiệu lực
        Optional<PaymentRequest> existingPending = paymentRequestRepository.findByObligationIdAndStatus(obligationId, PaymentRequest.RequestStatus.PENDING);
        if (existingPending.isPresent()) {
            PaymentRequest pr = existingPending.get();
            if (pr.getExpiresAt().isAfter(LocalDateTime.now())) {
                // Tái sử dụng request còn hiệu lực
                return mapToRequestResponse(pr);
            } else {
                // Đã hết hạn -> chuyển trạng thái EXPIRED để tạo request mới
                pr.setStatus(PaymentRequest.RequestStatus.EXPIRED);
                paymentRequestRepository.save(pr);
            }
        }

        // Tạo request mới
        String reference = "BM" + obligation.getId() + "_" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        PaymentRequest pr = new PaymentRequest();
        pr.setObligationId(obligation.getId());
        pr.setApartmentId(obligation.getApartmentId());
        pr.setCreatedByAccountId(accountId != null ? accountId : 1L);
        pr.setAmount(obligation.getAmountDue());
        pr.setPaymentReference(reference);
        pr.setStatus(PaymentRequest.RequestStatus.PENDING);
        pr.setExpiresAt(LocalDateTime.now().plusMinutes(qrTimeoutMinutes));

        PaymentRequest saved = paymentRequestRepository.save(pr);
        return mapToRequestResponse(saved);
    }

    @Override
    @Transactional
    public PaymentRequestResponse createDonationPaymentRequest(Long donationCampaignId, CreateDonationPaymentRequestDto request, Long accountId) {
        DonationCampaign campaign = donationCampaignRepository.findById(donationCampaignId)
                .orElseThrow(() -> new AppException("CAMPAIGN_NOT_FOUND", "Không tìm thấy đợt vận động đóng góp", HttpStatus.NOT_FOUND));

        if (campaign.getStatus() != DonationCampaign.DonationStatus.OPEN) {
            throw new AppException("CAMPAIGN_CLOSED", "Đợt vận động này đã kết thúc", HttpStatus.BAD_REQUEST);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("INVALID_DONATION_AMOUNT", "Số tiền đóng góp phải lớn hơn 0", HttpStatus.BAD_REQUEST);
        }

        apartmentRepository.findById(request.getApartmentId())
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ đóng góp", HttpStatus.NOT_FOUND));

        String reference = "BMD" + campaign.getId() + "_" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        PaymentRequest pr = new PaymentRequest();
        pr.setDonationCampaignId(campaign.getId());
        pr.setApartmentId(request.getApartmentId());
        pr.setCreatedByAccountId(accountId != null ? accountId : 1L);
        pr.setAmount(request.getAmount());
        pr.setPaymentReference(reference);
        pr.setStatus(PaymentRequest.RequestStatus.PENDING);
        pr.setExpiresAt(LocalDateTime.now().plusMinutes(qrTimeoutMinutes));

        PaymentRequest saved = paymentRequestRepository.save(pr);
        return mapToRequestResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentRequestResponse getPaymentRequestById(Long id) {
        PaymentRequest pr = paymentRequestRepository.findById(id)
                .orElseThrow(() -> new AppException("PAYMENT_REQUEST_NOT_FOUND", "Không tìm thấy yêu cầu thanh toán", HttpStatus.NOT_FOUND));
        return mapToRequestResponse(pr);
    }

    @Override
    @Transactional
    public PaymentRequestResponse cancelPaymentRequest(Long id, Long accountId) {
        PaymentRequest pr = paymentRequestRepository.findById(id)
                .orElseThrow(() -> new AppException("PAYMENT_REQUEST_NOT_FOUND", "Không tìm thấy yêu cầu thanh toán", HttpStatus.NOT_FOUND));

        if (pr.getStatus() != PaymentRequest.RequestStatus.PENDING) {
            throw new AppException("INVALID_REQUEST_STATUS", "Chỉ có thể hủy yêu cầu đang ở trạng thái PENDING", HttpStatus.BAD_REQUEST);
        }

        pr.setStatus(PaymentRequest.RequestStatus.CANCELLED);
        PaymentRequest updated = paymentRequestRepository.save(pr);
        return mapToRequestResponse(updated);
    }

    // ==========================================
    // 3. WEBHOOK SEPAY & ĐỐI SOÁT TỰ ĐỘNG (PHASE 2)
    // ==========================================

    @Override
    @Transactional
    public void processWebhook(SepayWebhookPayload payload) {
        if (payload == null || payload.getId() == null) {
            log.warn("Webhook SePay gửi payload rỗng hoặc thiếu ID giao dịch");
            return;
        }

        String externalId = String.valueOf(payload.getId());
        if (bankTransactionRepository.existsByExternalTransactionId(externalId)) {
            log.info("Giao dịch SePay {} đã tồn tại (idempotency check), bỏ qua xử lý lặp", externalId);
            return;
        }

        // 1. Lưu bản ghi BankTransaction ban đầu
        BankTransaction tx = new BankTransaction();
        tx.setExternalTransactionId(externalId);
        tx.setGateway(BankTransaction.Gateway.SEPAY);
        tx.setAccountNumber(payload.getAccountNumber() != null ? payload.getAccountNumber() : bankAccountNumber);
        tx.setSubAccount(payload.getSubAccount());
        tx.setAmount(payload.getTransferAmount() != null ? payload.getTransferAmount() : BigDecimal.ZERO);
        tx.setDirection("in".equalsIgnoreCase(payload.getTransferType()) ? BankTransaction.Direction.IN : BankTransaction.Direction.OUT);
        tx.setContent(payload.getContent() != null ? payload.getContent() : "");
        tx.setReferenceCode(payload.getReferenceCode());
        tx.setTransactionDate(parseTransactionDate(payload.getTransactionDate()));
        tx.setProcessingStatus(BankTransaction.ProcessingStatus.RECEIVED);
        tx.setNotes("Đã tiếp nhận từ SePay Webhook");

        BankTransaction savedTx = bankTransactionRepository.save(tx);

        // 2. Kiểm tra tính hợp lệ cơ bản
        if (savedTx.getDirection() != BankTransaction.Direction.IN || savedTx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            savedTx.setProcessingStatus(BankTransaction.ProcessingStatus.INVALID);
            savedTx.setNotes("Giao dịch không hợp lệ: không phải dòng tiền vào hoặc số tiền <= 0");
            bankTransactionRepository.save(savedTx);
            return;
        }

        // 3. Tìm mã tham chiếu trong nội dung chuyển khoản
        String content = (savedTx.getContent() + " " + (savedTx.getReferenceCode() != null ? savedTx.getReferenceCode() : "")).toUpperCase();
        Optional<PaymentRequest> matchedRequestOpt = findMatchingPaymentRequest(content);

        if (matchedRequestOpt.isEmpty()) {
            savedTx.setProcessingStatus(BankTransaction.ProcessingStatus.UNMATCHED);
            savedTx.setNotes("Không tìm thấy mã tham chiếu thanh toán hợp lệ (dạng BM... hoặc BMD...) trong nội dung chuyển khoản: " + savedTx.getContent());
            bankTransactionRepository.save(savedTx);
            log.warn("Giao dịch SePay {} không khớp với bất kỳ PaymentRequest PENDING nào", externalId);
            return;
        }

        PaymentRequest pr = matchedRequestOpt.get();

        // 4. Kiểm tra thời hạn hiệu lực của PaymentRequest
        if (pr.getExpiresAt().isBefore(LocalDateTime.now())) {
            pr.setStatus(PaymentRequest.RequestStatus.EXPIRED);
            paymentRequestRepository.save(pr);

            savedTx.setProcessingStatus(BankTransaction.ProcessingStatus.INVALID);
            savedTx.setPaymentRequestId(pr.getId());
            savedTx.setNotes("Yêu cầu thanh toán " + pr.getPaymentReference() + " đã hết hạn tại thời điểm nhận tiền");
            bankTransactionRepository.save(savedTx);
            log.warn("Yêu cầu thanh toán {} đã hết hạn khi nhận webhook SePay", pr.getPaymentReference());
            return;
        }

        // 5. Kiểm tra số tiền chuyển khoản có khớp 100% không
        if (savedTx.getAmount().compareTo(pr.getAmount()) != 0) {
            savedTx.setProcessingStatus(BankTransaction.ProcessingStatus.INVALID);
            savedTx.setPaymentRequestId(pr.getId());
            savedTx.setNotes("Số tiền chuyển khoản (" + savedTx.getAmount() + ") không khớp với số tiền yêu cầu (" + pr.getAmount() + ")");
            bankTransactionRepository.save(savedTx);
            log.warn("Số tiền SePay không khớp với PaymentRequest {}: nhận {} nhưng yêu cầu {}", pr.getPaymentReference(), savedTx.getAmount(), pr.getAmount());
            return;
        }

        // 6. ĐỐI SOÁT THÀNH CÔNG: Cập nhật PaymentRequest & Tạo Payment
        pr.setStatus(PaymentRequest.RequestStatus.MATCHED);
        paymentRequestRepository.save(pr);

        Payment payment = new Payment();
        payment.setObligationId(pr.getObligationId());
        payment.setDonationCampaignId(pr.getDonationCampaignId());
        payment.setApartmentId(pr.getApartmentId());
        payment.setAmount(savedTx.getAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.QR);
        payment.setPaymentStatus(Payment.PaymentStatus.CONFIRMED);
        payment.setPaymentSource(Payment.PaymentSource.AUTOMATIC);
        payment.setPaidAt(savedTx.getTransactionDate());
        payment.setRecordedByAccountId(1L); // Tài khoản hệ thống
        payment.setNotes("Thanh toán QR tự động qua SePay (GD: " + externalId + ", Mã ref: " + pr.getPaymentReference() + ")");

        Payment savedPayment = paymentRepository.save(payment);

        // Gạch nợ nghĩa vụ phí bắt buộc
        if (pr.getObligationId() != null) {
            feeObligationRepository.findById(pr.getObligationId()).ifPresent(obl -> {
                obl.setStatus(FeeObligation.ObligationStatus.PAID);
                obl.setPaymentStatus(FeeObligation.PaymentStatus.PAID);
                obl.setPaidAt(savedTx.getTransactionDate());
                feeObligationRepository.save(obl);
            });
        }

        // Cập nhật tổng tiền nhận của đợt đóng góp
        if (pr.getDonationCampaignId() != null) {
            donationCampaignRepository.findById(pr.getDonationCampaignId()).ifPresent(camp -> {
                BigDecimal current = camp.getTotalReceived() != null ? camp.getTotalReceived() : BigDecimal.ZERO;
                camp.setTotalReceived(current.add(savedTx.getAmount()));
                donationCampaignRepository.save(camp);
            });
        }

        // Cập nhật BankTransaction hoàn tất đối soát
        savedTx.setProcessingStatus(BankTransaction.ProcessingStatus.MATCHED);
        savedTx.setPaymentRequestId(pr.getId());
        savedTx.setPaymentId(savedPayment.getId());
        savedTx.setNotes("Đối soát tự động thành công và đã gạch nợ thành công");
        bankTransactionRepository.save(savedTx);

        log.info("Đối soát tự động thành công cho SePay TX {}, đã tạo phiếu thu #{}", externalId, savedPayment.getId());
    }

    @Override
    @Transactional
    public void processMockWebhook(MockSepayWebhookRequest request) {
        if (bankTransactionRepository.existsByExternalTransactionId(request.getTransactionId())) {
            throw new AppException("DUPLICATE_TRANSACTION", "Giao dịch kiểm thử " + request.getTransactionId() + " đã tồn tại", HttpStatus.CONFLICT);
        }

        SepayWebhookPayload payload = new SepayWebhookPayload();
        try {
            payload.setId(Long.parseLong(request.getTransactionId()));
        } catch (NumberFormatException e) {
            payload.setId(System.currentTimeMillis());
        }
        payload.setGateway("MOCK_SEPAY");
        payload.setAccountNumber(request.getAccountNumber() != null ? request.getAccountNumber() : bankAccountNumber);
        payload.setTransferAmount(request.getAmount());
        payload.setTransferType("in");
        payload.setContent(request.getContent());
        payload.setReferenceCode(request.getReferenceCode());
        payload.setTransactionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        processWebhook(payload);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankTransactionResponse> getAllBankTransactions(String status) {
        List<BankTransaction> list;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                BankTransaction.ProcessingStatus ps = BankTransaction.ProcessingStatus.valueOf(status.toUpperCase());
                list = bankTransactionRepository.findByProcessingStatusOrderByTransactionDateDesc(ps);
            } catch (Exception e) {
                list = bankTransactionRepository.findAllByOrderByTransactionDateDesc();
            }
        } else {
            list = bankTransactionRepository.findAllByOrderByTransactionDateDesc();
        }

        return list.stream().map(this::mapToBankTransactionResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankTransactionResponse getBankTransactionById(Long id) {
        BankTransaction tx = bankTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException("TRANSACTION_NOT_FOUND", "Không tìm thấy giao dịch ngân hàng", HttpStatus.NOT_FOUND));
        return mapToBankTransactionResponse(tx);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Optional<PaymentRequest> findMatchingPaymentRequest(String content) {
        // Pattern 1: Tìm exact format BM12_XXXX hoặc BMD5_XXXX
        Pattern pattern = Pattern.compile("(BMD?\\d+_[A-Z0-9]+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String ref = matcher.group(1);
            Optional<PaymentRequest> byRef = paymentRequestRepository.findByPaymentReference(ref);
            if (byRef.isPresent()) {
                return byRef;
            }
        }

        // Pattern 2: Tìm theo tất cả PaymentRequest PENDING trong hệ thống
        List<PaymentRequest> pendingList = paymentRequestRepository.findByStatus(PaymentRequest.RequestStatus.PENDING);
        for (PaymentRequest pr : pendingList) {
            if (content.contains(pr.getPaymentReference().toUpperCase())) {
                return Optional.of(pr);
            }
        }

        return Optional.empty();
    }

    private String generateQrCodeUrl(BigDecimal amount, String paymentReference) {
        try {
            String encodedAccountName = URLEncoder.encode(bankAccountName, StandardCharsets.UTF_8);
            String encodedRef = URLEncoder.encode(paymentReference, StandardCharsets.UTF_8);
            return String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                    bankId, bankAccountNumber, amount.toPlainString(), encodedRef, encodedAccountName);
        } catch (Exception e) {
            return "";
        }
    }

    private PaymentRequestResponse mapToRequestResponse(PaymentRequest pr) {
        PaymentRequestResponse res = new PaymentRequestResponse();
        res.setId(pr.getId());
        res.setObligationId(pr.getObligationId());
        res.setDonationCampaignId(pr.getDonationCampaignId());
        res.setApartmentId(pr.getApartmentId());
        res.setAmount(pr.getAmount());
        res.setPaymentReference(pr.getPaymentReference());
        res.setStatus(pr.getStatus().name());
        res.setExpiresAt(pr.getExpiresAt());
        res.setBankId(bankId);
        res.setAccountNumber(bankAccountNumber);
        res.setAccountName(bankAccountName);
        res.setQrCodeUrl(generateQrCodeUrl(pr.getAmount(), pr.getPaymentReference()));
        res.setCreatedAt(pr.getCreatedAt());

        if (pr.getApartmentId() != null) {
            apartmentRepository.findById(pr.getApartmentId())
                    .ifPresent(a -> res.setApartmentNumber(a.getApartmentNumber()));
        }

        if (pr.getObligationId() != null) {
            feeObligationRepository.findById(pr.getObligationId()).ifPresent(obl -> {
                collectionCampaignRepository.findById(obl.getCampaignId()).ifPresent(c -> res.setCampaignTitle(c.getTitle()));
            });
        } else if (pr.getDonationCampaignId() != null) {
            donationCampaignRepository.findById(pr.getDonationCampaignId()).ifPresent(d -> res.setCampaignTitle(d.getTitle()));
        }

        return res;
    }

    private BankTransactionResponse mapToBankTransactionResponse(BankTransaction tx) {
        BankTransactionResponse res = new BankTransactionResponse();
        res.setId(tx.getId());
        res.setExternalTransactionId(tx.getExternalTransactionId());
        res.setGateway(tx.getGateway() != null ? tx.getGateway().name() : null);
        res.setAccountNumber(tx.getAccountNumber());
        res.setSubAccount(tx.getSubAccount());
        res.setAmount(tx.getAmount());
        res.setDirection(tx.getDirection() != null ? tx.getDirection().name() : null);
        res.setContent(tx.getContent());
        res.setReferenceCode(tx.getReferenceCode());
        res.setTransactionDate(tx.getTransactionDate());
        res.setProcessingStatus(tx.getProcessingStatus() != null ? tx.getProcessingStatus().name() : null);
        res.setPaymentRequestId(tx.getPaymentRequestId());
        res.setPaymentId(tx.getPaymentId());
        res.setNotes(tx.getNotes());
        res.setCreatedAt(tx.getCreatedAt());
        return res;
    }

    private LocalDateTime parseTransactionDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ex) {
                return LocalDateTime.now();
            }
        }
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

        if (p.getApartmentId() != null) {
            apartmentRepository.findById(p.getApartmentId())
                    .ifPresent(apt -> res.setApartmentNumber(apt.getApartmentNumber()));
        }

        if (p.getPayerPersonId() != null) {
            personRepository.findById(p.getPayerPersonId())
                    .ifPresent(person -> res.setPayerName(person.getFullName()));
        }

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
