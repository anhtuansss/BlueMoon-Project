package com.bluemoon.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateApartmentRequest {

    @NotBlank(message = "Số căn hộ không được để trống")
    private String apartmentNumber;

    @NotNull(message = "Diện tích không được để trống")
    @DecimalMin(value = "0.1", message = "Diện tích phải lớn hơn 0")
    private BigDecimal area;

    public CreateApartmentRequest() {}

    public CreateApartmentRequest(String apartmentNumber, BigDecimal area) {
        this.apartmentNumber = apartmentNumber;
        this.area = area;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }
}
