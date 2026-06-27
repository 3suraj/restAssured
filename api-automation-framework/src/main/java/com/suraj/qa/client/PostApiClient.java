package com.suraj.qa.client;

import com.suraj.qa.assertions.PostAssertions;
import com.suraj.qa.model.request.PostRequest;
import io.restassured.response.Response;

public class PostApiClient extends ApiClient {

    public PostApiClient() {
        super("/posts");
    }

    public PostAssertions create(PostRequest request) {
        Response response = post("", request);
        return new PostAssertions(response);
    }

    public PostAssertions getById(int id) {
        Response response = get("/" + id);
        return new PostAssertions(response);
    }

    public PostAssertions getAll() {
        Response response = get("");
        return new PostAssertions(response);
    }

    public PostAssertions update(int id, PostRequest request) {
        Response response = put("/" + id, request);
        return new PostAssertions(response);
    }

    public PostAssertions deleteById(int id) {
        Response response = delete("/" + id);
        return new PostAssertions(response);
    }
}