package com.bluemoon.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransactionResponse {
    private Long id;
    private String externalTransactionId;
    private String gateway;
    private String accountNumber;
    private String subAccount;
    private BigDecimal amount;
    private String direction;
    private String content;
    private String referenceCode;
    private LocalDateTime transactionDate;
    private String processingStatus;
    private Long paymentRequestId;
    private Long paymentId;
    private String notes;
    private LocalDateTime createdAt;

    public BankTransactionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExternalTransactionId() { return externalTransactionId; }
    public void setExternalTransactionId(String externalTransactionId) { this.externalTransactionId = externalTransactionId; }

    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getSubAccount() { return subAccount; }
    public void setSubAccount(String subAccount) { this.subAccount = subAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getProcessingStatus() { return processingStatus; }
    public void setProcessingStatus(String processingStatus) { this.processingStatus = processingStatus; }

    public Long getPaymentRequestId() { return paymentRequestId; }
    public void setPaymentRequestId(Long paymentRequestId) { this.paymentRequestId = paymentRequestId; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
