package com.tibber.dev.revolutintegration.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.model.RefreshTokenResponse;
import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import com.tibber.dev.revolutintegration.model.TransactionData;
import org.springframework.batch.item.ItemReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RevolutTransactionDataReader implements ItemReader<TransactionData> {

    private final String transactionApiUrl;
    private final String refreshTokenApiUrl;
    private final String authFilePath;
    private ObjectMapper objectMapper;
    private int nextDataIndex;
    private List<TransactionData> transactionData;
    private RefreshTokenResponse tokenResponse;

    public RevolutTransactionDataReader(String transactionApiUrl, String refreshTokenApiUrl, String authFilePath) {
        this.transactionApiUrl = transactionApiUrl;
        this.refreshTokenApiUrl = refreshTokenApiUrl;
        this.authFilePath = authFilePath;
        nextDataIndex = 0;
        objectMapper = new ObjectMapper();
        // ignore unknown properties
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public TransactionData read() {
        if (accessTokenIsNotInitialized()) {
            tokenResponse = fetchAccessTokenFromAPI();
        }

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

    private boolean accessTokenIsNotInitialized() {
        return this.tokenResponse == null;
    }

    private boolean transactionDataIsNotInitialized() {
        return this.transactionData == null;
    }

    private List<TransactionData> fetchTransactionDataFromAPI() {
        List<TransactionData> list = null;
        // todo: build url dynamically with from and to
        LocalDate from = LocalDate.parse("2017-06-01");
        String[] command = new String[]{
                "curl",
                transactionApiUrl + "?from=" + from.toString() + "&to=" + getLastDate().toString(),
                "-H",
                "Authorization: Bearer " + tokenResponse.getAccessToken()
        };

        String response = sendRequest(command);
        // todo: if result is "{"message":"The request should be authorized."}" handle error
        try {
            list = parseStringToTransactionDataList(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return list;
    }

    private RefreshTokenResponse fetchAccessTokenFromAPI() {
        RefreshTokenResponse result = null;

        RevolutAuthInfo authInfo = getAuthInfoFromFile();
        System.out.println(authInfo);

        String[] command = new String[]{
                "curl",
                refreshTokenApiUrl,
                "-H",
                "Content-Type: application/x-www-form-urlencoded",
                "--data", "grant_type=refresh_token",
                "--data", "refresh_token=" + authInfo.getRefreshToken(),
                "--data", "client_id=" + authInfo.getClientId(),
                "--data", "client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                "--data", "client_assertion=" + authInfo.getJwt(),
        };

        String response = sendRequest(command);
        try {
            result = parseStringToRefreshTokenResponse(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private String sendRequest(String[] command) {
        StringBuilder textBuilder = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // set working directory to
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            // convert input stream to string
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
                System.out.println(textBuilder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textBuilder.toString();
    }

    private RefreshTokenResponse parseStringToRefreshTokenResponse(String json) throws JsonProcessingException {
        RefreshTokenResponse response = null;
        response = objectMapper.readValue(json, RefreshTokenResponse.class);
        System.out.println("response: " + response);
        return response;
    }

    private List<TransactionData> parseStringToTransactionDataList(String json) throws JsonProcessingException {
        List<TransactionData> list = null;
        list = Arrays.asList(objectMapper.readValue(json, TransactionData[].class));
        System.out.println("list: " + list);
        return list;
    }

    private LocalDate getLastDate() {
        LocalDate today = LocalDate.now();
        return today.minus(Period.ofDays(1));
    }

    private RevolutAuthInfo getAuthInfoFromFile() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(authFilePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        RevolutAuthInfo authInfo = null;
        try {
            authInfo = objectMapper.readValue(contentBuilder.toString(), RevolutAuthInfo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return authInfo;
    }
}
