package com.tibber.dev.revolutintegration.writer;

import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class TransactionDataLineAggregatorTest {

    private TransactionDataLineAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new TransactionDataLineAggregator();
    }

    @Test
    void aggregate() throws JSONException {
        FlattenTransactionData transactionData = getTestFlattenTransactionData();
        String expected = "{id:testId,cardFirstName:testFirstName,cardLastName:testLastName,type:transfer,requestId:null,state:pending}";
        String result = aggregator.aggregate(transactionData);
        JSONAssert.assertEquals(expected, result, JSONCompareMode.LENIENT);
    }

    private FlattenTransactionData getTestFlattenTransactionData() {
        FlattenTransactionData transactionData = new FlattenTransactionData();
        transactionData.setId("testId");
        transactionData.setCardFirstName("testFirstName");
        transactionData.setCardLastName("testLastName");
        transactionData.setType("transfer");
        transactionData.setState("pending");
        return transactionData;
    }
}