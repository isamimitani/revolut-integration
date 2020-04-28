package com.tibber.dev.revolutintegration.reader;

import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper class to map auth information from database to Java object.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
public class RevolutAuthInfoDataRowMapper implements RowMapper<RevolutAuthInfo> {

    @Override
    public RevolutAuthInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        return new RevolutAuthInfo(
                resultSet.getString("refresh_token"),
                resultSet.getString("client_id"),
                resultSet.getString("jwt")
        );
    }
}
