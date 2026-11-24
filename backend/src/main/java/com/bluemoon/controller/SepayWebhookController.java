package com.bluemoon.controller;

import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.SepayWebhookPayload;
import com.bluemoon.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller tiếp nhận Webhook từ SePay và Mock Webhook kiểm thử (Thành viên 4 - Tuấn - F06).
 */
@RestController
@RequestMapping("/api/v1/integrations/sepay")
public class SepayWebhookController {

    private final PaymentService paymentService;

    public SepayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody SepayWebhookPayload payload) {
        // SePay webhook - xác thực theo cấu hình SePay
        paymentService.processWebhook(payload);
        return ResponseEntity.ok(Map.of("success", true, "message", "Webhook received"));
    }

    @PostMapping("/mock-webhook")
    public ResponseEntity<ApiResponse<String>> handleMockWebhook(@Valid @RequestBody MockSepayWebhookRequest request) {
        paymentService.processMockWebhook(request);
        return ResponseEntity.ok(ApiResponse.success("Mock webhook processed successfully"));
    }
}
