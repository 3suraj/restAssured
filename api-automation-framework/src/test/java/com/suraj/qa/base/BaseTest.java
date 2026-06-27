package com.suraj.qa.base;

import com.suraj.qa.config.EnvironmentConfig;
import com.suraj.qa.config.RequestContext;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class BaseTest {

    @BeforeEach
    void setUpContext(TestInfo testInfo) {
        RequestContext.init();
        // Removed problematic Allure labels that cause Instant overflow with Java 21
        // Allure automatically captures test name and metadata without manual labels
        Allure.parameter("environment", EnvironmentConfig.env());
        Allure.parameter("thread", Thread.currentThread().getName());
    }

    @AfterEach
    void tearDownContext() {
        RequestContext.clear(); // prevent memory leaks in parallel runs
    }
}
