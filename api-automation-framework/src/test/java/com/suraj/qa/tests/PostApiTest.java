package com.suraj.qa.tests;

import com.suraj.qa.base.BaseTest;
import com.suraj.qa.client.PostApiClient;
import com.suraj.qa.factory.PostRequestFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

@Epic("Posts API")
@Feature("CRUD Operations")
class PostApiTest extends BaseTest {

    private final PostApiClient postApi = new PostApiClient();

    @Test
    @Story("Create post")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should create a post and return 201 with an id")
    @Tag("smoke")
    void shouldCreatePostSuccessfully() {
        postApi.create(PostRequestFactory.validPost())
                .assertStatusCreated()
                .assertHasId()
                .assertTitleEquals("Automation Test Post")
                .assertContentTypeJson();
    }

    @Test
    @Story("Get post")
    @DisplayName("Should retrieve an existing post by id")
    @Tag("regression")
    void shouldGetPostById() {
        postApi.getById(1)
                .assertStatusOk()
                .assertHasId()
                .assertUserIdEquals(1)
                .assertContentTypeJson();
    }

    @Test
    @Story("Create post")
    @DisplayName("Should create post for specific user")
    @Tag("regression")
    void shouldCreatePostForSpecificUser() {
        postApi.create(PostRequestFactory.postForUser(5))
                .assertStatusCreated()
                .assertUserIdEquals(5);
    }

    @Test
    @Story("Delete post")
    @DisplayName("Should delete a post and return 200")
    @Tag("regression")
    void shouldDeletePost() {
        postApi.deleteById(1)
                .assertStatusOk();
    }
}