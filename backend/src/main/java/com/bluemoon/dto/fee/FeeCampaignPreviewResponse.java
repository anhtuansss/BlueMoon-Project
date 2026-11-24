package com.bluemoon.dto.fee;

import java.math.BigDecimal;
import java.util.List;

public class FeeCampaignPreviewResponse {
    private BigDecimal appliedUnitPrice;
    private Integer apartmentCount;
    private BigDecimal totalAmount;
    private List<FeeObligationPreviewItemDto> items;

    public FeeCampaignPreviewResponse() {}

    public FeeCampaignPreviewResponse(BigDecimal appliedUnitPrice, Integer apartmentCount, BigDecimal totalAmount, List<FeeObligationPreviewItemDto> items) {
        this.appliedUnitPrice = appliedUnitPrice;
        this.apartmentCount = apartmentCount;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public BigDecimal getAppliedUnitPrice() { return appliedUnitPrice; }
    public void setAppliedUnitPrice(BigDecimal appliedUnitPrice) { this.appliedUnitPrice = appliedUnitPrice; }

    public Integer getApartmentCount() { return apartmentCount; }
    public void setApartmentCount(Integer apartmentCount) { this.apartmentCount = apartmentCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<FeeObligationPreviewItemDto> getItems() { return items; }
    public void setItems(List<FeeObligationPreviewItemDto> items) { this.items = items; }
}
