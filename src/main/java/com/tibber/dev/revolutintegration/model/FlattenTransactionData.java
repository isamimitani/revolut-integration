package com.tibber.dev.revolutintegration.model;

import java.util.Date;

/**
 * A model class to hold flattened transaction data.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
public class FlattenTransactionData {

    private String id;
    private String type;
    private String requestId;
    private String state;
    private String reasonCode;
    private Date createdAt;
    private Date updatedAt;
    private Date completedAt;
    private String scheduledFor;
    private String relatedTransactionId;
    private String reference;
    private String legId;
    private String legAmount;
    private String legCurrency;
    private String legBillAmount;
    private String legBillCurrency;
    private String legAccountId;
    private String legCounterpartyId;
    private String legCounterpartyAccountId;
    private String legCounterpartyAccountType;
    private String legDescription;
    private String legBalance;
    private String legFee;
    private String legId2;
    private String legAccountId2;
    private String legAmount2;
    private String legCurrency2;
    private String legDescription2;
    private String legBalance2;
    private String cardNumber;
    private String cardFirstName;
    private String cardLastName;
    private String cardPhone;
    private String merchantName;
    private String merchantCity;
    private String merchantCategoryCode;
    private String merchantCountry;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(String scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(String relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLegId() {
        return legId;
    }

    public void setLegId(String legId) {
        this.legId = legId;
    }

    public String getLegAmount() {
        return legAmount;
    }

    public void setLegAmount(String legAmount) {
        this.legAmount = legAmount;
    }

    public String getLegCurrency() {
        return legCurrency;
    }

    public void setLegCurrency(String legCurrency) {
        this.legCurrency = legCurrency;
    }

    public String getLegBillAmount() {
        return legBillAmount;
    }

    public void setLegBillAmount(String legBillAmount) {
        this.legBillAmount = legBillAmount;
    }

    public String getLegBillCurrency() {
        return legBillCurrency;
    }

    public void setLegBillCurrency(String legBillCurrency) {
        this.legBillCurrency = legBillCurrency;
    }

    public String getLegAccountId() {
        return legAccountId;
    }

    public void setLegAccountId(String legAccountId) {
        this.legAccountId = legAccountId;
    }

    public String getLegCounterpartyId() {
        return legCounterpartyId;
    }

    public void setLegCounterpartyId(String legCounterpartyId) {
        this.legCounterpartyId = legCounterpartyId;
    }

    public String getLegCounterpartyAccountId() {
        return legCounterpartyAccountId;
    }

    public void setLegCounterpartyAccountId(String legCounterpartyAccountId) {
        this.legCounterpartyAccountId = legCounterpartyAccountId;
    }

    public String getLegCounterpartyAccountType() {
        return legCounterpartyAccountType;
    }

    public void setLegCounterpartyAccountType(String legCounterpartyAccountType) {
        this.legCounterpartyAccountType = legCounterpartyAccountType;
    }

    public String getLegDescription() {
        return legDescription;
    }

    public void setLegDescription(String legDescription) {
        this.legDescription = legDescription;
    }

    public String getLegBalance() {
        return legBalance;
    }

    public void setLegBalance(String legBalance) {
        this.legBalance = legBalance;
    }

    public String getLegFee() {
        return legFee;
    }

    public void setLegFee(String legFee) {
        this.legFee = legFee;
    }

    public String getLegId2() {
        return legId2;
    }

    public void setLegId2(String legId2) {
        this.legId2 = legId2;
    }

    public String getLegAccountId2() {
        return legAccountId2;
    }

    public void setLegAccountId2(String legAccountId2) {
        this.legAccountId2 = legAccountId2;
    }

    public String getLegAmount2() {
        return legAmount2;
    }

    public void setLegAmount2(String legAmount2) {
        this.legAmount2 = legAmount2;
    }

    public String getLegCurrency2() {
        return legCurrency2;
    }

    public void setLegCurrency2(String legCurrency2) {
        this.legCurrency2 = legCurrency2;
    }

    public String getLegDescription2() {
        return legDescription2;
    }

    public void setLegDescription2(String legDescription2) {
        this.legDescription2 = legDescription2;
    }

    public String getLegBalance2() {
        return legBalance2;
    }

    public void setLegBalance2(String legBalance2) {
        this.legBalance2 = legBalance2;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardFirstName() {
        return cardFirstName;
    }

    public void setCardFirstName(String cardFirstName) {
        this.cardFirstName = cardFirstName;
    }

    public String getCardLastName() {
        return cardLastName;
    }

    public void setCardLastName(String cardLastName) {
        this.cardLastName = cardLastName;
    }

    public String getCardPhone() {
        return cardPhone;
    }

    public void setCardPhone(String cardPhone) {
        this.cardPhone = cardPhone;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getMerchantCountry() {
        return merchantCountry;
    }

    public void setMerchantCountry(String merchantCountry) {
        this.merchantCountry = merchantCountry;
    }

    @Override
    public String toString() {
        return "FlattenTransactionData{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", requestId='" + requestId + '\'' +
                ", state='" + state + '\'' +
                ", reasonCode='" + reasonCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", completedAt=" + completedAt +
                ", scheduledFor='" + scheduledFor + '\'' +
                ", relatedTransactionId='" + relatedTransactionId + '\'' +
                ", reference='" + reference + '\'' +
                ", legId='" + legId + '\'' +
                ", legAmount='" + legAmount + '\'' +
                ", legCurrency='" + legCurrency + '\'' +
                ", legBillAmount='" + legBillAmount + '\'' +
                ", legBillCurrency='" + legBillCurrency + '\'' +
                ", legAccountId='" + legAccountId + '\'' +
                ", legCounterpartyId='" + legCounterpartyId + '\'' +
                ", legCounterpartyAccountId='" + legCounterpartyAccountId + '\'' +
                ", legCounterpartyAccountType='" + legCounterpartyAccountType + '\'' +
                ", legDescription='" + legDescription + '\'' +
                ", legBalance='" + legBalance + '\'' +
                ", legFee='" + legFee + '\'' +
                ", legId2='" + legId2 + '\'' +
                ", legAccountId2='" + legAccountId2 + '\'' +
                ", legAmount2='" + legAmount2 + '\'' +
                ", legCurrency2='" + legCurrency2 + '\'' +
                ", legDescription2='" + legDescription2 + '\'' +
                ", legBalance2='" + legBalance2 + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardFirstName='" + cardFirstName + '\'' +
                ", cardLastName='" + cardLastName + '\'' +
                ", cardPhone='" + cardPhone + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantCity='" + merchantCity + '\'' +
                ", merchantCategoryCode='" + merchantCategoryCode + '\'' +
                ", merchantCountry='" + merchantCountry + '\'' +
                '}';
    }
}
