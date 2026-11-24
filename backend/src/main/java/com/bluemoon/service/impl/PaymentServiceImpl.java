package com.bluemoon.service.impl;

import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.PaymentResponse;
import com.bluemoon.dto.payment.SepayWebhookPayload;
import com.bluemoon.entity.BankTransaction;
import com.bluemoon.entity.FeeObligation;
import com.bluemoon.entity.Payment;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.BankTransactionRepository;
import com.bluemoon.repository.FeeObligationRepository;
import com.bluemoon.repository.PaymentRepository;
import com.bluemoon.repository.PaymentRequestRepository;
import com.bluemoon.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation cho Quản lý thanh toán và Mock Webhook SePay (Thành viên 4 - Tuấn - F06).
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeObligationRepository feeObligationRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              FeeObligationRepository feeObligationRepository,
                              PaymentRequestRepository paymentRequestRepository,
                              BankTransactionRepository bankTransactionRepository) {
        this.paymentRepository = paymentRepository;
        this.feeObligationRepository = feeObligationRepository;
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

        Payment.PaymentMethod method;
        try {
            method = Payment.PaymentMethod.valueOf(request.getMethod().toUpperCase());
            if (method == Payment.PaymentMethod.QR) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new AppException("INVALID_PAYMENT_METHOD", "Phương thức thanh toán thủ công chỉ chấp nhận CASH hoặc BANK_TRANSFER", HttpStatus.BAD_REQUEST);
        }

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
    public void processWebhook(SepayWebhookPayload payload) {
        // TODO [Tuấn - Week 1]: Xử lý webhook từ SePay (idempotency qua external_transaction_id, kiểm tra gateway, đối soát PaymentRequest PENDING)
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
        // TODO [Tuấn - Week 1]: Hoàn thiện mock webhook phục vụ kiểm thử luồng thanh toán QR tự động
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

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse res = new PaymentResponse();
        res.setId(p.getId());
        res.setObligationId(p.getObligationId());
        res.setApartmentId(p.getApartmentId());
        res.setAmount(p.getAmount());
        res.setPaymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null);
        res.setPaymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null);
        res.setPaymentSource(p.getPaymentSource() != null ? p.getPaymentSource().name() : null);
        res.setPaidAt(p.getPaidAt());
        res.setRecordedByAccountId(p.getRecordedByAccountId());
        res.setNotes(p.getNotes());
        return res;
    }
}
