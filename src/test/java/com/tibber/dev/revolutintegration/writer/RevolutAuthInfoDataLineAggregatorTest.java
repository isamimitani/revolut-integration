package com.tibber.dev.revolutintegration.writer;

import com.tibber.dev.revolutintegration.model.RevolutAuthInfo;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class RevolutAuthInfoDataLineAggregatorTest {

    private RevolutAuthInfoDataLineAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new RevolutAuthInfoDataLineAggregator();
    }

    @Test
    void aggregate() throws JSONException {
        RevolutAuthInfo authInfo = new RevolutAuthInfo();
        authInfo.setRefreshToken("testRefreshToken");
        authInfo.setClientId("testClientId");
        authInfo.setJwt("testJWT");

        String expected = "{refreshToken:testRefreshToken,clientId:testClientId,jwt:testJWT}";
        String result = aggregator.aggregate(authInfo);
        System.out.println(result);
        JSONAssert.assertEquals(expected, result, JSONCompareMode.STRICT);
    }
}