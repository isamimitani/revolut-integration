package com.tibber.dev.revolutintegration.reader;

import com.tibber.dev.revolutintegration.entity.TransactionData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDataRowMapper implements RowMapper<TransactionData> {

    @Override
    public TransactionData mapRow(ResultSet resultSet, int i) throws SQLException {
        return new TransactionData(
                resultSet.getString("id"),
                resultSet.getString("type"),
                resultSet.getString("requestedId"),
                resultSet.getString("state"),
                resultSet.getString("createdAt"),
                resultSet.getString("updatedAt"),
                resultSet.getString("completedAt"),
                resultSet.getString("reference"),
                null
        );
    }
}
