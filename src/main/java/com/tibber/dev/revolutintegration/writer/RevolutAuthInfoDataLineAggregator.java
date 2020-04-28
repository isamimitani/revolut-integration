package com.tibber.dev.revolutintegration.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * A line aggregator class to aggregate {@link com.tibber.dev.revolutintegration.model.RevolutAuthInfo} to Json string.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
public class RevolutAuthInfoDataLineAggregator implements LineAggregator<RevolutAuthInfo> {

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts {@code RevolutAuthInfo} object to Json string
     *
     * @param authInfo
     * @return Json string
     */
    @Override
    public String aggregate(RevolutAuthInfo authInfo) {
        try {
            return objectMapper.writeValueAsString(authInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to serialize data", e);
        }
    }
}
