package com.bluemoon.service;

import com.bluemoon.dto.payment.*;

import java.util.List;

public interface PaymentService {
    // 1. Thu phí thủ công
    PaymentResponse recordManualPayment(Long obligationId, ManualPaymentRequest request, Long recordedByAccountId);
    PaymentResponse recordManualDonationPayment(Long donationCampaignId, ManualPaymentRequest request, Long recordedByAccountId);
    List<PaymentResponse> getAllPayments(Long apartmentId, Long obligationId, Long donationCampaignId);
    PaymentResponse getPaymentById(Long id);

    // 2. Tạo & Tra cứu Payment Request (Mã QR)
    PaymentRequestResponse createFeePaymentRequest(Long obligationId, Long accountId);
    PaymentRequestResponse createDonationPaymentRequest(Long donationCampaignId, CreateDonationPaymentRequestDto request, Long accountId);
    PaymentRequestResponse getPaymentRequestById(Long id);
    PaymentRequestResponse cancelPaymentRequest(Long id, Long accountId);

    // 3. Webhook SePay & Đối soát tự động
    void processWebhook(SepayWebhookPayload payload);
    void processMockWebhook(MockSepayWebhookRequest request);
    List<BankTransactionResponse> getAllBankTransactions(String status);
    BankTransactionResponse getBankTransactionById(Long id);
}
