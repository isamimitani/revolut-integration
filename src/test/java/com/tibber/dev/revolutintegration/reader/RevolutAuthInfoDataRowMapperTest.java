package com.tibber.dev.revolutintegration.reader;

import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RevolutAuthInfoDataRowMapperTest {

    private RevolutAuthInfoDataRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        rowMapper = new RevolutAuthInfoDataRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("refresh_token")).thenReturn("testToken");
        when(resultSet.getString("client_id")).thenReturn("testClientId");
        when(resultSet.getString("jwt")).thenReturn("testJWT");

        RevolutAuthInfo authInfo = rowMapper.mapRow(resultSet, 1);
        assertEquals("testToken", authInfo.getRefreshToken());
        assertEquals("testClientId", authInfo.getClientId());
        assertEquals("testJWT", authInfo.getJwt());
    }

    @Test
    void mapRowWithNullData() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("refresh_token")).thenReturn(null);
        when(resultSet.getString("client_id")).thenReturn(null);
        when(resultSet.getString("jwt")).thenReturn(null);

        RevolutAuthInfo authInfo = rowMapper.mapRow(resultSet, 1);
        assertEquals(null, authInfo.getRefreshToken());
        assertEquals(null, authInfo.getClientId());
        assertEquals(null, authInfo.getJwt());
    }

    @Test
    void mapRowWithInvalidColumnName() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("refresh_token")).thenThrow(SQLException.class);

        Exception exception = assertThrows(SQLException.class, () -> {
            rowMapper.mapRow(resultSet, 1);
        });
    }
}