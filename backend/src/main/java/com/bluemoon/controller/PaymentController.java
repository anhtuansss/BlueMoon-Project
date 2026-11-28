package com.bluemoon.controller;

import com.bluemoon.dto.payment.ManualPaymentRequest;
import com.bluemoon.dto.payment.PaymentResponse;
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

    /**
     * Ghi nhận thu phí bắt buộc thủ công (Tiền mặt / Chuyển khoản)
     */
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

    /**
     * Ghi nhận đóng góp tự nguyện thủ công
     */
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

    /**
     * Tra cứu danh sách các thanh toán đã ghi nhận
     */
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

    /**
     * Xem chi tiết 1 phiếu thanh toán
     */
    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
