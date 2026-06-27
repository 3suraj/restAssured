package com.suraj.qa.factory;

import com.suraj.qa.model.request.PostRequest;

public class PostRequestFactory {

    // Default builder — override only what each test needs
    public static PostRequest.PostRequestBuilder defaultPost() {
        return PostRequest.builder()
                .title("Automation Test Post")
                .body("This post was created by the API automation framework.")
                .userId(1);
    }

    // Named scenarios — expressive, DRY, reusable
    public static PostRequest validPost() {
        return defaultPost().build();
    }

    public static PostRequest postWithLongTitle() {
        return defaultPost()
                .title("A".repeat(255))
                .build();
    }

    public static PostRequest postWithMissingTitle() {
        return defaultPost()
                .title(null)   // title deliberately missing
                .build();
    }

    public static PostRequest postForUser(int userId) {
        return defaultPost()
                .userId(userId)
                .build();
    }
}
