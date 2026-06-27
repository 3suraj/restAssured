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
        Allure.label("environment", EnvironmentConfig.env());
        Allure.label("thread",      Thread.currentThread().getName());
        Allure.label("test",        testInfo.getDisplayName());
    }

    @AfterEach
    void tearDownContext() {
        RequestContext.clear(); // prevent memory leaks in parallel runs
    }
}