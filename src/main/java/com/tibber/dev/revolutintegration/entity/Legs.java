package com.tibber.dev.revolutintegration.entity;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Legs {

    private String legId;
    private String amount;
    private String currency;
    private String billAmount;
    private String billCurrency;
    private String accountId;
    private Map<String, Object> counterparty = new LinkedHashMap<>();
    private String description;
    private String balance;
    private String fee;

    public String getLegId() {
        return legId;
    }

    @JsonSetter("leg_id")
    public void setLegId(String legId) {
        this.legId = legId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillAmount() {
        return billAmount;
    }

    @JsonSetter("bill_amount")
    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getBillCurrency() {
        return billCurrency;
    }

    @JsonSetter("bill_currency")
    public void setBillCurrency(String billCurrency) {
        this.billCurrency = billCurrency;
    }

    public String getAccountId() {
        return accountId;
    }

    @JsonSetter("account_id")
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<String, Object> getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Map<String, Object> counter_party) {
        this.counterparty = counter_party;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "Legs{" +
                "leg_id='" + legId + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", bill_amount='" + billAmount + '\'' +
                ", bill_currency='" + billCurrency + '\'' +
                ", account_id='" + accountId + '\'' +
                ", counterparty=" + counterparty +
                ", description='" + description + '\'' +
                ", balance='" + balance + '\'' +
                ", fee='" + fee + '\'' +
                '}';
    }
}
