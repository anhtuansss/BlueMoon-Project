package com.bluemoon.dto.fee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeCampaignPreviewRequest {

    @NotNull(message = "feeTypeId không được để trống")
    private Long feeTypeId;

    @NotBlank(message = "campaignName không được để trống")
    private String campaignName;

    private String feePeriod; // 'YYYY-MM'

    @NotNull(message = "collectionStartDate không được để trống")
    private LocalDate collectionStartDate;

    @NotNull(message = "dueDate không được để trống")
    private LocalDate dueDate;

    private BigDecimal customUnitPrice; // optional if overriding default_unit_price
    private String description;
    private CampaignTargetDto target;

    public FeeCampaignPreviewRequest() {}

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }

    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }

    public String getFeePeriod() { return feePeriod; }
    public void setFeePeriod(String feePeriod) { this.feePeriod = feePeriod; }

    public LocalDate getCollectionStartDate() { return collectionStartDate; }
    public void setCollectionStartDate(LocalDate collectionStartDate) { this.collectionStartDate = collectionStartDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getCustomUnitPrice() { return customUnitPrice; }
    public void setCustomUnitPrice(BigDecimal customUnitPrice) { this.customUnitPrice = customUnitPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CampaignTargetDto getTarget() { return target; }
    public void setTarget(CampaignTargetDto target) { this.target = target; }
}
