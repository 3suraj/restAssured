package com.suraj.qa.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.function.Supplier;

public class RetryHandler {

    private static final Logger log = LoggerFactory.getLogger(RetryHandler.class);

    public static <T> T withRetry(Supplier<T> action, int maxAttempts, Duration backoff) {
        Throwable lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.get();
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        // Exponential backoff: 1x, 2x, 3x...
                        Thread.sleep(backoff.toMillis() * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        throw new RuntimeException("All " + maxAttempts + " attempts failed", lastException);
    }
}

// Usage in tests — for async operations:
// import static org.awaitility.Awaitility.*;
// import static java.util.concurrent.TimeUnit.*;
//
// await().atMost(30, SECONDS).pollInterval(500, MILLISECONDS)
//     .until(() -> postApi.getById(id).getRawResponse().statusCode() == 200);
