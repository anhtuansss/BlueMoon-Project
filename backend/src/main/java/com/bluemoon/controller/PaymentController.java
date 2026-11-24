package com.bluemoon.controller;

import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.PaymentResponse;
import com.bluemoon.security.UserPrincipal;
import com.bluemoon.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho Quản lý thu tiền & thanh toán thủ công (Thành viên 4 - Tuấn - F06).
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/fee-obligations/{id}/payments")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordManualPayment(
            @PathVariable("id") Long obligationId,
            @Valid @RequestBody ManualPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        PaymentResponse response = paymentService.recordManualPayment(obligationId, request, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }
}
