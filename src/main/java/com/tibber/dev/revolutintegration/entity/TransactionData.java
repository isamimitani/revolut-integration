package com.tibber.dev.revolutintegration.entity;

public class TransactionData {

    private String id;
    private String type;
    private String requestedId;
    private String state;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
    private String reference;
    private String[] legs;

    public TransactionData(){}

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

    public String getRequestedId() {
        return requestedId;
    }

    public void setRequestedId(String requestedId) {
        this.requestedId = requestedId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String[] getLegs() {
        return legs;
    }

    public void setLegs(String[] legs) {
        this.legs = legs;
    }

    @Override
    public String toString() {
        return "TransactionData{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
