package com.bluemoon.service;

import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.PaymentResponse;
import com.bluemoon.dto.payment.SepayWebhookPayload;

public interface PaymentService {
    PaymentResponse recordManualPayment(Long obligationId, ManualPaymentRequest request, Long recordedByAccountId);
    void processWebhook(SepayWebhookPayload payload);
    void processMockWebhook(MockSepayWebhookRequest request);
}
