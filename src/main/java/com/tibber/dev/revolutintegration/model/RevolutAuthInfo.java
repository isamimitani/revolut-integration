package com.tibber.dev.revolutintegration.model;

/**
 * A model class to hold auth information from database.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
public class RevolutAuthInfo {

    private String refreshToken;
    private String clientId;
    private String jwt;

    public RevolutAuthInfo() {
    }

    public RevolutAuthInfo(String refreshToken, String clientId, String jwt) {
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public String toString() {
        return "RevolutAuthInfo{" +
                "refreshToken='" + refreshToken + '\'' +
                ", clientId='" + clientId + '\'' +
                ", jwt='" + jwt + '\'' +
                '}';
    }
}
