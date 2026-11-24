package com.bluemoon.dto.fee;

import java.math.BigDecimal;

public class FeeObligationPreviewItemDto {
    private Long apartmentId;
    private String apartmentNumber;
    private BigDecimal areaSnapshot;
    private BigDecimal unitPriceSnapshot;
    private BigDecimal amountDue;

    public FeeObligationPreviewItemDto() {}

    public FeeObligationPreviewItemDto(Long apartmentId, String apartmentNumber, BigDecimal areaSnapshot, BigDecimal unitPriceSnapshot, BigDecimal amountDue) {
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.areaSnapshot = areaSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.amountDue = amountDue;
    }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }

    public BigDecimal getAreaSnapshot() { return areaSnapshot; }
    public void setAreaSnapshot(BigDecimal areaSnapshot) { this.areaSnapshot = areaSnapshot; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
}
