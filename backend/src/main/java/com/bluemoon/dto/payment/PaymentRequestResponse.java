package com.bluemoon.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequestResponse {
    private Long id;
    private Long obligationId;
    private Long donationCampaignId;
    private String campaignTitle;
    private Long apartmentId;
    private String apartmentNumber;
    private BigDecimal amount;
    private String paymentReference;
    private String status;
    private LocalDateTime expiresAt;
    private String qrCodeUrl;
    private String bankId;
    private String accountNumber;
    private String accountName;
    private LocalDateTime createdAt;

    public PaymentRequestResponse() {}

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

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
