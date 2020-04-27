package com.tibber.dev.revolutintegration.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.model.RefreshTokenResponse;
import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import com.tibber.dev.revolutintegration.model.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

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

    private static final Logger log = LoggerFactory.getLogger(RevolutTransactionDataReader.class);

    private final Environment environment;
    private final JdbcTemplate jdbcTemplate;
    private final String transactionApiUrl;
    private final String refreshTokenApiUrl;
    private LocalDate from;
    private LocalDate to;
    private ObjectMapper objectMapper;
    private int nextDataIndex;
    private List<TransactionData> transactionData;
    private RefreshTokenResponse tokenResponse;

    public RevolutTransactionDataReader(Environment environment, JdbcTemplate jdbcTemplate, String fromDate, String toDate) {
        this.environment = environment;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionApiUrl = environment.getRequiredProperty("revolut.api.url.transaction");
        this.refreshTokenApiUrl = environment.getRequiredProperty("revolut.api.url.refreshtoken");
        nextDataIndex = 0;

        from = getDateMinusDays(Integer.parseInt(environment.getRequiredProperty("days.latest")));
        to = getDateMinusDays(1);
        if (fromDate != null) {
            from = LocalDate.parse(fromDate);
        }
        if (toDate != null) {
            to = LocalDate.parse(toDate);
        }

        objectMapper = new ObjectMapper();
        // ignore unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public TransactionData read() {
        if (accessTokenIsNotInitialized()) {
            tokenResponse = fetchAccessTokenFromAPI();
        }

        if (tokenResponse.getError() != null) {
            log.error("Failed to get access token. Terminate batch job. error: " + tokenResponse.getError() + ", error_description: " + tokenResponse.getErrorDescription());
            throw new RuntimeException("Failed to get access token. Terminate batch job. error: " + tokenResponse.getError() + ", error_description: " + tokenResponse.getErrorDescription());
        }

        if (transactionDataIsNotInitialized()) {
            transactionData = fetchTransactionDataFromAPI();
            removeTransactionDataFromDB();
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
        String[] command = new String[]{
                "curl",
                transactionApiUrl + "?from=" + from.toString() + "&to=" + to.toString(),
                "-H",
                "Authorization: Bearer " + tokenResponse.getAccessToken()
        };
        System.out.println(command[1]);

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
        log.info(authInfo.toString());

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
        // todo: handle {"error":"invalid_request","error_description":"The Token has expired on Thu Apr 23 10:23:47 UTC 2020."}
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

    private LocalDate getDateMinusDays(int days) {
        LocalDate today = LocalDate.now();
        return today.minus(Period.ofDays(days));
    }

    private RevolutAuthInfo getAuthInfoFromFile() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(environment.getRequiredProperty("revolut.auth.file.path")), StandardCharsets.UTF_8)) {
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

    private void removeTransactionDataFromDB() {
        LocalDate tomorrow = to.plus(Period.ofDays(1));
        log.debug("delete transaction data from " + from.toString() + " to " + tomorrow.toString());
        int deletedRows = jdbcTemplate.update(environment.getRequiredProperty("sql.delete.transactiondata"),
                new Object[]{from.toString(), to.toString()});
        log.debug("Deleted " + deletedRows + " rows.");
    }
}
