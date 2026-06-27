package com.suraj.qa.config;

import com.suraj.qa.auth.TokenManager;
import com.suraj.qa.filter.MaskedLoggingFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import java.util.UUID;

public class RequestContext {

    // Each thread gets its own copy — the key to parallel safety
    private static final ThreadLocal<RequestSpecification> SPEC          = new ThreadLocal<>();
    private static final ThreadLocal<String>  CORRELATION_ID = new ThreadLocal<>();

    public static void init() {
        String corrId = UUID.randomUUID().toString();
        CORRELATION_ID.set(corrId);
        SPEC.set(new RequestSpecBuilder()
                .setBaseUri(EnvironmentConfig.baseUrl())
                .addHeader("Authorization",    "Bearer " + TokenManager.getInstance().getToken())
                .addHeader("X-Correlation-ID", corrId)
                .addHeader("Content-Type",     "application/json")
                .addHeader("Accept",           "application/json")
                .setRelaxedHTTPSValidation()
                .addFilter(new MaskedLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build());
    }

    public static RequestSpecification get()  { return SPEC.get(); }
    public static String correlationId()       { return CORRELATION_ID.get(); }

    // Call in @AfterEach to prevent memory leaks
    public static void clear() {
        SPEC.remove();
        CORRELATION_ID.remove();
    }
}
