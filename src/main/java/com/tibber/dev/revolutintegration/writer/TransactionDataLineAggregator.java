package com.tibber.dev.revolutintegration.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.entity.FlattenTransactionData;
import com.tibber.dev.revolutintegration.entity.TransactionData;
import org.springframework.batch.item.file.transform.LineAggregator;

//public class TestDataLineAggregator implements LineAggregator<TransactionData> {
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public String aggregate(TransactionData transactionData) {
//        try {
//            return objectMapper.writeValueAsString(transactionData);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("unable to serialize data", e);
//        }
//    }
//}

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
