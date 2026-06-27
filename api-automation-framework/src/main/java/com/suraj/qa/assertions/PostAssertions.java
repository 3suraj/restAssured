package com.suraj.qa.assertions;

import com.suraj.qa.model.response.PostResponse;
import io.restassured.response.Response;
import static org.junit.jupiter.api.Assertions.*;

public class PostAssertions {

    private final Response response;

    public PostAssertions(Response response) {
        this.response = response;
    }

    public PostAssertions assertStatusOk() {
        assertEquals(200, response.statusCode(),
                "Expected 200 but got " + response.statusCode());
        return this;
    }

    public PostAssertions assertStatusCreated() {
        assertEquals(201, response.statusCode(),
                "Expected 201 but got " + response.statusCode());
        return this;
    }

    public PostAssertions assertStatusNotFound() {
        assertEquals(404, response.statusCode());
        return this;
    }

    public PostAssertions assertHasId() {
        PostResponse body = response.as(PostResponse.class);
        assertNotNull(body.getId(), "Response must contain an id");
        assertTrue(body.getId() > 0, "Id must be positive");
        return this;
    }

    public PostAssertions assertTitleEquals(String expectedTitle) {
        PostResponse body = response.as(PostResponse.class);
        assertEquals(expectedTitle, body.getTitle(),
                "Title mismatch");
        return this;
    }

    public PostAssertions assertUserIdEquals(int expectedUserId) {
        PostResponse body = response.as(PostResponse.class);
        assertEquals(expectedUserId, body.getUserId());
        return this;
    }

    public PostAssertions assertContentTypeJson() {
        assertTrue(response.contentType().contains("application/json"),
                "Expected JSON content type");
        return this;
    }

    public PostResponse getBody() {
        return response.as(PostResponse.class);
    }

    public Response getRawResponse() {
        return response;
    }
}