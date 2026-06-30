package com.billing.platform.dto;

import java.math.BigDecimal;

public class RecordPaymentRequest {
    private BigDecimal amount;
    private String method;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}