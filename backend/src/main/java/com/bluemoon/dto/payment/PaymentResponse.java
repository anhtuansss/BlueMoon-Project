package com.bluemoon.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private Long obligationId;
    private Long donationCampaignId;
    private String campaignTitle;
    private Long apartmentId;
    private String apartmentNumber;
    private Long payerPersonId;
    private String payerName;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentSource;
    private LocalDateTime paidAt;
    private Long recordedByAccountId;
    private String notes;

    public PaymentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getObligationId() { return obligationId; }
    public void setObligationId(Long obligationId) { this.obligationId = obligationId; }

    public Long getDonationCampaignId() { return donationCampaignId; }
    public void setDonationCampaignId(Long donationCampaignId) { this.donationCampaignId = donationCampaignId; }

    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }

    public Long getPayerPersonId() { return payerPersonId; }
    public void setPayerPersonId(Long payerPersonId) { this.payerPersonId = payerPersonId; }

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentSource() { return paymentSource; }
    public void setPaymentSource(String paymentSource) { this.paymentSource = paymentSource; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public Long getRecordedByAccountId() { return recordedByAccountId; }
    public void setRecordedByAccountId(Long recordedByAccountId) { this.recordedByAccountId = recordedByAccountId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
