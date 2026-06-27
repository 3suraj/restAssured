package com.suraj.qa.auth;

import com.suraj.qa.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

public class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);
    private static final TokenManager INSTANCE = new TokenManager();

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    private TokenManager() {}

    public static TokenManager getInstance() { return INSTANCE; }

    public String getToken() {
        if (isExpired()) refreshToken();
        return cachedToken;
    }

    private boolean isExpired() {
        // Refresh 30 seconds before actual expiry to avoid mid-test failures
        return Instant.now().isAfter(expiresAt.minusSeconds(30));
    }

    private synchronized void refreshToken() {
        if (!isExpired()) return; // double-checked locking — another thread may have refreshed
        log.info("Refreshing OAuth2 token for env: {}", EnvironmentConfig.env());
        Response r = RestAssured.given()
                .formParam("grant_type",    "client_credentials")
                .formParam("client_id",     EnvironmentConfig.get("oauth.client.id"))
                .formParam("client_secret", EnvironmentConfig.get("oauth.client.secret"))
                .post(EnvironmentConfig.get("oauth.token.url"));
        cachedToken = r.jsonPath().getString("access_token");
        int ttl    = r.jsonPath().getInt("expires_in");
        expiresAt  = Instant.now().plusSeconds(ttl);
        log.info("Token refreshed, expires in {}s", ttl);
    }
}

// NOTE: For local testing against jsonplaceholder.typicode.com (no real OAuth),
// we can stub getToken() to return "fake-token" during development.
