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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

/**
 * A reader class to read transaction data from Revolut Business API.
 * It refreshes access token before fetching transaction data and
 * deleting old data from database when fetching is completed
 *
 * @auther Isami Mitani
 * @version 1.0
 */
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

        from = getDateMinusDays(Integer.parseInt(environment.getRequiredProperty("days.before.from")));
        to = getDateMinusDays(Integer.parseInt(environment.getRequiredProperty("days.before.to")));
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

    /**
     * Initializes access token and transaction data and reads transaction data one by one.
     * Deletes also old transaction data when fetching is completed.
     *
     * @return {@code TransactionData}
     */
    @Override
    public TransactionData read() {
        if (accessTokenIsNotInitialized()) {
            tokenResponse = fetchAccessTokenFromAPI();
        }

        if (tokenResponse.getError() != null) {
            String errorMessage = "Failed to get access token. Terminate batch job. error: " + tokenResponse.getError() + ", error_description: " + tokenResponse.getErrorDescription();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
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


    /**
     * @return {@code boolean} whether access token is fetched or not.
     */
    private boolean accessTokenIsNotInitialized() {
        return this.tokenResponse == null;
    }

    /**
     * @return {@code boolean} whether transaction data is fetched or not.
     */
    private boolean transactionDataIsNotInitialized() {
        return this.transactionData == null;
    }


    /**
     * Calls Revolut API and fetches transaction data for specified dates
     *
     * @return {@code List} of fetched {@code Transactiondata}
     */
    private List<TransactionData> fetchTransactionDataFromAPI() {
        List<TransactionData> list;
        String[] command = new String[]{
                "curl",
                transactionApiUrl + "?from=" + from.toString() + "&to=" + to.toString(),
                "-H",
                "Authorization: Bearer " + tokenResponse.getAccessToken()
        };
        System.out.println(command[1]);

        String response = sendRequest(command);
        // if result contains "{"message":"The request should be authorized."}" throw exception
        if(response.contains("message")) {
            throw new RuntimeException(response);
        }
        try {
            list = parseStringToTransactionDataList(response);
            log.info("Fetched transaction data size: " + list.size());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    /**
     * Calls Revolut API and refreshes access token
     *
     * @return {@code RefreshTokenResponse}
     */
    private RefreshTokenResponse fetchAccessTokenFromAPI() {
        RefreshTokenResponse result;

        RevolutAuthInfo authInfo = getAuthInfoFromFile();
        log.debug(authInfo.toString());

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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Sends received request command and converts response to string
     *
     * @param command
     * @return response string
     */
    private String sendRequest(String[] command) {
        StringBuilder textBuilder = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // set working directory to current directory
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
            }
            System.out.println(textBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return textBuilder.toString();
    }

    /**
     * Parses received Json string to {@code RefreshTokenResponse} object
     *
     * @param json
     * @return {@code RefreshTokenResponse}
     * @throws JsonProcessingException
     */
    public RefreshTokenResponse parseStringToRefreshTokenResponse(String json) throws JsonProcessingException {
        RefreshTokenResponse response;
        response = objectMapper.readValue(json, RefreshTokenResponse.class);
        log.debug("response: " + response);
        return response;
    }

    /**
     * Parses received Json string to {@code List<TransactionData>} object
     *
     * @param json
     * @return {@code List<TransactionData>}
     * @throws JsonProcessingException
     */
    private List<TransactionData> parseStringToTransactionDataList(String json) throws JsonProcessingException {
        List<TransactionData> list;
        list = Arrays.asList(objectMapper.readValue(json, TransactionData[].class));
        log.debug("list: " + list);
        return list;
    }

    /**
     * Returns new {@code LocalDate} object contains current date subtracted by specified number of days
     *
     * @param days
     * @return {@code LocalDate}
     */
    private LocalDate getDateMinusDays(int days) {
        LocalDate today = LocalDate.now();
        return today.minus(Period.ofDays(days));
    }

    /**
     * Reads auth information from temporary file and returns {@code RevolutAuthInfo} object
     *
     * @return {@code RevolutAuthInfo}
     */
    private RevolutAuthInfo getAuthInfoFromFile() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(environment.getRequiredProperty("revolut.auth.file.path")), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        RevolutAuthInfo authInfo;
        try {
            authInfo = objectMapper.readValue(contentBuilder.toString(), RevolutAuthInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return authInfo;
    }

    /**
     * Deletes old transaction data from database
     */
    private void removeTransactionDataFromDB() {
        LocalDate tomorrow = to.plus(Period.ofDays(1));
        log.debug("Deleting transaction data from " + from.toString() + " to " + to.toString());
        int deletedRows = jdbcTemplate.update(environment.getRequiredProperty("sql.delete.transactiondata"),
                new Object[]{from.toString(), tomorrow.toString()});
        log.debug("Deleted " + deletedRows + " rows.");
    }
}
