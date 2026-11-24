package com.bluemoon.dto.fee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeeCampaignResponse {
    private Long id;
    private Long feeTypeId;
    private String feeTypeName;
    private String title;
    private String cyclePeriod;
    private BigDecimal appliedUnitPrice;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String status;
    private Integer totalApartments;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public FeeCampaignResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }

    public String getFeeTypeName() { return feeTypeName; }
    public void setFeeTypeName(String feeTypeName) { this.feeTypeName = feeTypeName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCyclePeriod() { return cyclePeriod; }
    public void setCyclePeriod(String cyclePeriod) { this.cyclePeriod = cyclePeriod; }

    public BigDecimal getAppliedUnitPrice() { return appliedUnitPrice; }
    public void setAppliedUnitPrice(BigDecimal appliedUnitPrice) { this.appliedUnitPrice = appliedUnitPrice; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalApartments() { return totalApartments; }
    public void setTotalApartments(Integer totalApartments) { this.totalApartments = totalApartments; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
