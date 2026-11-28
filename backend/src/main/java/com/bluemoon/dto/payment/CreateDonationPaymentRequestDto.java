package com.bluemoon.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateDonationPaymentRequestDto {

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1.0", message = "Số tiền đóng góp phải lớn hơn 0")
    private BigDecimal amount;

    @NotNull(message = "Cần chọn căn hộ thực hiện đóng góp")
    private Long apartmentId;

    public CreateDonationPaymentRequestDto() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }
}
