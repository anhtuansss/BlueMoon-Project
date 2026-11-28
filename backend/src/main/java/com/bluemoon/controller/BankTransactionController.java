package com.bluemoon.controller;

import com.bluemoon.dto.payment.BankTransactionResponse;
import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý biến động giao dịch ngân hàng qua SePay (Thành viên 4 - Tuấn - F06).
 */
@RestController
@RequestMapping("/api/v1/bank-transactions")
public class BankTransactionController {

    private final PaymentService paymentService;

    public BankTransactionController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Tra cứu danh sách giao dịch ngân hàng nhận từ SePay (Lọc theo trạng thái: RECEIVED, MATCHED, UNMATCHED, INVALID)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<List<BankTransactionResponse>>> getAllBankTransactions(
            @RequestParam(required = false) String status
    ) {
        List<BankTransactionResponse> transactions = paymentService.getAllBankTransactions(status);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * Xem chi tiết 1 biến động giao dịch ngân hàng
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<BankTransactionResponse>> getBankTransactionById(@PathVariable Long id) {
        BankTransactionResponse response = paymentService.getBankTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
