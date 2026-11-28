package com.bluemoon.service;

import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.PaymentResponse;
import com.bluemoon.dto.payment.SepayWebhookPayload;

import java.util.List;

public interface PaymentService {
    PaymentResponse recordManualPayment(Long obligationId, ManualPaymentRequest request, Long recordedByAccountId);
    PaymentResponse recordManualDonationPayment(Long donationCampaignId, ManualPaymentRequest request, Long recordedByAccountId);
    List<PaymentResponse> getAllPayments(Long apartmentId, Long obligationId, Long donationCampaignId);
    PaymentResponse getPaymentById(Long id);
    void processWebhook(SepayWebhookPayload payload);
    void processMockWebhook(MockSepayWebhookRequest request);
}
