package com.bluemoon.dto.response;

import java.math.BigDecimal;

public class ApartmentDto {
    private String apartmentId;
    private String apartmentNumber;
    private BigDecimal area;
    private Boolean isActive;

    public ApartmentDto() {}

    public ApartmentDto(String apartmentId, String apartmentNumber, BigDecimal area, Boolean isActive) {
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.area = area;
        this.isActive = isActive;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
