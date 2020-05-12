package com.tibber.dev.revolutintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A model class to hold transaction data from Revolut API.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionData {

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
    private List<Legs> legs;
    private Map<String, Object> card = new LinkedHashMap<>();
    private Map<String, Object> merchant = new LinkedHashMap<>();

    public TransactionData() {
    }

    public TransactionData(String id, String type) {
        this.id = id;
        this.type = type;
    }

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

    @JsonSetter("request_id")
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

    @JsonSetter("reason_code")
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @JsonSetter("created_at")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonSetter("updated_at")
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    @JsonSetter("completed_at")
    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getScheduledFor() {
        return scheduledFor;
    }

    @JsonSetter("scheduled_for")
    public void setScheduledFor(String scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getRelatedTransactionId() {
        return relatedTransactionId;
    }

    @JsonSetter("related_transaction_id")
    public void setRelatedTransactionId(String relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }

    public Map<String, Object> getCard() {
        return card;
    }

    public void setCard(Map<String, Object> card) {
        this.card = card;
    }

    public Map<String, Object> getMerchant() {
        return merchant;
    }

    public void setMerchant(Map<String, Object> merchant) {
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "TransactionData{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", request_id='" + requestId + '\'' +
                ", state='" + state + '\'' +
                ", reason_code='" + reasonCode + '\'' +
                ", created_at='" + createdAt + '\'' +
                ", updated_at='" + updatedAt + '\'' +
                ", completed_at='" + completedAt + '\'' +
                ", scheduled_for='" + scheduledFor + '\'' +
                ", related_transaction_id='" + relatedTransactionId + '\'' +
                ", reference='" + reference + '\'' +
                ", card=" + card +
                ", merchant=" + merchant +
                '}';
    }
}
