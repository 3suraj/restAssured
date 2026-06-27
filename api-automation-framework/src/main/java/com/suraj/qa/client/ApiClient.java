package com.suraj.qa.client;

import com.suraj.qa.config.RequestContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public abstract class ApiClient {

    protected final String basePath;

    protected ApiClient(String basePath) {
        this.basePath = basePath;
    }

    protected Response get(String path) {
        return RestAssured.given(RequestContext.get())
                .get(basePath + path);
    }

    protected Response get(String path, Map<String, ?> queryParams) {
        return RestAssured.given(RequestContext.get())
                .queryParams(queryParams)
                .get(basePath + path);
    }

    protected Response post(String path, Object body) {
        return RestAssured.given(RequestContext.get())
                .body(body)
                .post(basePath + path);
    }

    protected Response put(String path, Object body) {
        return RestAssured.given(RequestContext.get())
                .body(body)
                .put(basePath + path);
    }

    protected Response delete(String path) {
        return RestAssured.given(RequestContext.get())
                .delete(basePath + path);
    }
}
