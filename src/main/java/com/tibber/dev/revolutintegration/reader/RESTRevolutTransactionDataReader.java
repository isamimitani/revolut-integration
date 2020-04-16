package com.tibber.dev.revolutintegration.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.entity.TransactionData;
import org.springframework.batch.item.ItemReader;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class RESTRevolutTransactionDataReader implements ItemReader<TransactionData> {

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextDataIndex;
    private List<TransactionData> transactionData;

    public RESTRevolutTransactionDataReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextDataIndex = 0;
    }

    @Override
    public TransactionData read() {
        if (transactionDataIsNotInitialized()) {
            transactionData = fetchTransactionDataFromAPI();
        }

        TransactionData nextData = null;
        if (nextDataIndex < transactionData.size()) {
            nextData = transactionData.get(nextDataIndex);
            nextDataIndex++;
        }

        return nextData;
    }

    private boolean transactionDataIsNotInitialized() {
        return this.transactionData == null;
    }

    private List<TransactionData> fetchTransactionDataFromAPI() {
        List<TransactionData> list = null;
        // Prepare curl call to Revolut API
        // todo: build url dynamically
        String[] command = new String[]{"curl",
                "https://sandbox-b2b.revolut.com/api/1.0/transactions?count=10",
                "-H",
                "Authorization: Bearer oa_sand_rMZMQeB-2HfVz2pUa-RMOpl6fL96BvyXCcK2o3ezQYo"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // set working directory to
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            System.out.println("result: " + textBuilder);
            // todo: if result is "{"message":"The request should be authorized."}" handle error
            list = parseStringToTransactionDataList(textBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<TransactionData> parseStringToTransactionDataList(String json) throws JsonProcessingException {
        List<TransactionData> list = null;

        // parse retrieved string to Java object
        ObjectMapper objectMapper = new ObjectMapper();
        // ignore unknown properties
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        list = Arrays.asList(objectMapper.readValue(json, TransactionData[].class));
        System.out.println("list: " + list);
        return list;
    }
}
