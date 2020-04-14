package com.tibber.dev.revolutintegration.reader;

import com.tibber.dev.revolutintegration.entity.TestData;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class RESTRevolutTransactionDataReader implements ItemReader<TestData> {

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextDataIndex;
    private List<TestData> transactionData;

    public RESTRevolutTransactionDataReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextDataIndex = 0;
    }

    @Override
    public TestData read() throws Exception {
        if (transactionDataIsNotInitialized()) {
            transactionData = fetchTransactionDataFromAPI();
        }

        TestData nextData = null;

        if (nextDataIndex < transactionData.size()) {
            nextData = transactionData.get(nextDataIndex);
            nextDataIndex++;
        }

        return nextData;
    }

    private boolean transactionDataIsNotInitialized() {
        return this.transactionData == null;
    }

    private List<TestData> fetchTransactionDataFromAPI() {
        ResponseEntity<TestData[]> response = restTemplate.getForEntity(
                apiUrl,
                TestData[].class
        );
        System.out.println(response.getBody());
        TestData[] fetchedTransactionData = response.getBody();
        return Arrays.asList(fetchedTransactionData);
    }
}
