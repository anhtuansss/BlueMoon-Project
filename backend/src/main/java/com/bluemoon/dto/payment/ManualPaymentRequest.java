package com.bluemoon.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ManualPaymentRequest {

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1.0", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "Phương thức thanh toán không được để trống (CASH, BANK_TRANSFER)")
    private String method;

    private Long apartmentId; // Bắt buộc khi đóng góp tự nguyện (không qua obligation)
    private LocalDateTime paidAt;
    private Long payerPersonId;
    private String notes;

    public ManualPaymentRequest() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public Long getPayerPersonId() { return payerPersonId; }
    public void setPayerPersonId(Long payerPersonId) { this.payerPersonId = payerPersonId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
