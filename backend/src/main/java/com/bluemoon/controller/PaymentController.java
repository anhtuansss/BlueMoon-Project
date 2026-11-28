package com.bluemoon.controller;

import com.bluemoon.dto.payment.*;
import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.security.UserPrincipal;
import com.bluemoon.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho Quản lý thu tiền & thanh toán (Thành viên 4 - Tuấn - F06).
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ==========================================
    // 1. GHI NHẬN THANH TOÁN THỦ CÔNG
    // ==========================================

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

    @PostMapping("/donation-campaigns/{id}/payments")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordManualDonationPayment(
            @PathVariable("id") Long donationCampaignId,
            @Valid @RequestBody ManualPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        PaymentResponse response = paymentService.recordManualDonationPayment(donationCampaignId, request, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) Long apartmentId,
            @RequestParam(required = false) Long obligationId,
            @RequestParam(required = false) Long donationCampaignId
    ) {
        List<PaymentResponse> payments = paymentService.getAllPayments(apartmentId, obligationId, donationCampaignId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==========================================
    // 2. SINH MÃ QR VÀ PAYMENT REQUEST (PHASE 2)
    // ==========================================

    /**
     * Sinh yêu cầu thanh toán mã QR VietQR cho nghĩa vụ phí
     */
    @PostMapping("/fee-obligations/{id}/payment-requests")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentRequestResponse>> createFeePaymentRequest(
            @PathVariable("id") Long obligationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        PaymentRequestResponse response = paymentService.createFeePaymentRequest(obligationId, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    /**
     * Sinh yêu cầu thanh toán mã QR VietQR cho đóng góp tự nguyện
     */
    @PostMapping("/donation-campaigns/{id}/payment-requests")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentRequestResponse>> createDonationPaymentRequest(
            @PathVariable("id") Long donationCampaignId,
            @Valid @RequestBody CreateDonationPaymentRequestDto request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        PaymentRequestResponse response = paymentService.createDonationPaymentRequest(donationCampaignId, request, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    /**
     * Tra cứu trạng thái yêu cầu thanh toán QR
     */
    @GetMapping("/payment-requests/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentRequestResponse>> getPaymentRequestById(@PathVariable Long id) {
        PaymentRequestResponse response = paymentService.getPaymentRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Hủy yêu cầu thanh toán QR
     */
    @PostMapping("/payment-requests/{id}/cancel")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentRequestResponse>> cancelPaymentRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        PaymentRequestResponse response = paymentService.cancelPaymentRequest(id, accountId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
