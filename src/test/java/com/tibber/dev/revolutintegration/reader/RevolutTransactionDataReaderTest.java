package com.tibber.dev.revolutintegration.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tibber.dev.revolutintegration.model.RefreshTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RevolutTransactionDataReaderTest {

    private RevolutTransactionDataReader reader;

    private Environment environment = mock(Environment.class);
    private JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

    @BeforeEach
    void setUp() {
        when(environment.getRequiredProperty("revolut.api.url.transaction")).thenReturn("transactionURL");
        when(environment.getRequiredProperty("revolut.api.url.refreshtoken")).thenReturn("refreshTokenURL");
        when(environment.getRequiredProperty("days.before.from")).thenReturn("7");
        when(environment.getRequiredProperty("days.before.to")).thenReturn("1");

        reader = new RevolutTransactionDataReader(environment, jdbcTemplate, "2020-04-01", "2020-04-30");
    }

    @Test
    void parseStringToRefreshTokenResponse() throws JsonProcessingException {
        String jsonString = "{\"access_token\":\"testToken\",\"token_type\":\"testTokenType\",\"expires_in\":\"12345\"}";
        RefreshTokenResponse refreshTokenResponse = reader.parseStringToRefreshTokenResponse(jsonString);
        assertEquals("testToken", refreshTokenResponse.getAccessToken());
        assertEquals("testTokenType", refreshTokenResponse.getTokenType());
        assertEquals(12345, refreshTokenResponse.getExpiresIn());
    }
}