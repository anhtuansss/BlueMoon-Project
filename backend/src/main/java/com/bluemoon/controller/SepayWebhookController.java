package com.bluemoon.controller;

import com.bluemoon.dto.payment.MockSepayWebhookRequest;
import com.bluemoon.dto.payment.SepayWebhookPayload;
import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller tiếp nhận Webhook từ SePay và Mock Webhook kiểm thử (Thành viên 4 - Tuấn - F06).
 */
@RestController
@RequestMapping("/api/v1/integrations/sepay")
public class SepayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SepayWebhookController.class);

    private final PaymentService paymentService;

    @Value("${sepay.api.key:}")
    private String configuredApiKey;

    public SepayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Webhook nhận biến động số dư từ SePay IPN
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SepayWebhookPayload payload
    ) {
        // Kiểm tra API Key nếu cấu hình yêu cầu
        if (configuredApiKey != null && !configuredApiKey.isBlank()) {
            String expected = "Apikey " + configuredApiKey;
            if (authHeader == null || !authHeader.equalsIgnoreCase(expected)) {
                log.warn("SePay webhook unauthorized: Header Authorization không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Unauthorized"));
            }
        }

        paymentService.processWebhook(payload);
        return ResponseEntity.ok(Map.of("success", true, "message", "Webhook received and processed"));
    }

    /**
     * Mock Webhook phục vụ dev và tester kiểm thử trực tiếp
     */
    @PostMapping("/mock-webhook")
    public ResponseEntity<ApiResponse<String>> handleMockWebhook(@Valid @RequestBody MockSepayWebhookRequest request) {
        paymentService.processMockWebhook(request);
        return ResponseEntity.ok(ApiResponse.success("Mock webhook đã được xử lý và đối soát tự động thành công!"));
    }
}
