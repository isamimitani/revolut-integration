package com.tibber.dev.revolutintegration.processor;

import com.tibber.dev.revolutintegration.entity.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

//public class TransactionDataItemProcessor implements ItemProcessor<TransactionData, TransactionData> {
//
//    private static final Logger log = LoggerFactory.getLogger(TransactionDataItemProcessor.class);
//
//    @Override
//    public TransactionData process(final TransactionData transactionData) throws Exception {
//        final String id = transactionData.getId().toUpperCase();
//        final String type = transactionData.getType().toUpperCase();
//
//        final TransactionData newTransactionData = new TransactionData(id, type);
//
//        log.info("Converting (" + transactionData + ") into (" + newTransactionData + ")");
//
//        return newTransactionData;
//    }
//}

public class TransactionDataItemProcessor implements ItemProcessor<TestData, TestData> {

    private static final Logger log = LoggerFactory.getLogger(TransactionDataItemProcessor.class);

    @Override
    public TestData process(final TestData transactionData) throws Exception {
        final String id = transactionData.getId().toUpperCase();
        final String userId = transactionData.getUserId().toUpperCase();

        final TestData newTransactionData = new TestData(id, userId);

        log.info("Converting (" + transactionData + ") into (" + newTransactionData + ")");

        return newTransactionData;
    }
}

