package com.tibber.dev.revolutintegration.model;

import com.fasterxml.jackson.annotation.JsonSetter;

public class RefreshTokenResponse {

    private String accessToken;
    private String tokenType;
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    @JsonSetter("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    @JsonSetter("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    @JsonSetter("expires_in")
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "RefreshTokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
