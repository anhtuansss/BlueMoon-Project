package com.bluemoon.dto.fee;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FeeObligationDto {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private Long apartmentId;
    private String apartmentNumber;
    private Long feeTypeId;
    private String feeTypeName;
    private BigDecimal areaSnapshot;
    private BigDecimal unitPriceSnapshot;
    private BigDecimal amountDue;
    private String status;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public FeeObligationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }

    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }

    public String getFeeTypeName() { return feeTypeName; }
    public void setFeeTypeName(String feeTypeName) { this.feeTypeName = feeTypeName; }

    public BigDecimal getAreaSnapshot() { return areaSnapshot; }
    public void setAreaSnapshot(BigDecimal areaSnapshot) { this.areaSnapshot = areaSnapshot; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
