package com.bluemoon.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MockSepayWebhookRequest {

    @NotBlank(message = "transactionId không được để trống")
    private String transactionId;

    @NotNull(message = "amount không được để trống")
    @DecimalMin(value = "1.0", message = "amount phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "content không được để trống")
    private String content;

    private String accountNumber;
    private String referenceCode;

    public MockSepayWebhookRequest() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
}
