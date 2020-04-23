package com.tibber.dev.revolutintegration.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import org.springframework.batch.item.file.transform.LineAggregator;

public class TransactionDataLineAggregator implements LineAggregator<FlattenTransactionData> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String aggregate(FlattenTransactionData transactionData) {
        try {
            return objectMapper.writeValueAsString(transactionData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to serialize data", e);
        }
    }
}
