package com.suraj.qa.ai;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class TestCaseGenerator {

    private static final Logger log = LoggerFactory.getLogger(TestCaseGenerator.class);
    private final OpenAiService openAi;

    public TestCaseGenerator() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) throw new IllegalStateException("OPENAI_API_KEY env var not set");
        this.openAi = new OpenAiService(apiKey);
    }

    public String generateTests(String openApiYaml, String endpoint) {
        String prompt = String.format("""
            You are a Principal QA Engineer.
            Given this OpenAPI spec endpoint: %s
            For path: %s
            Generate JUnit 5 tests covering:
            1. Happy path (valid payload)
            2. Each required field missing (one test each)
            3. Boundary values for numeric fields
            4. Invalid enum values
            5. Missing/expired auth token
            Use our factory pattern: PostRequestFactory.defaultPost().build()
            Use our assertion DSL: postApi.create(request).assertStatusCreated()
            Add @Tag("generated") to all tests.
            Output ONLY valid Java code. No explanation.""",
                openApiYaml, endpoint);

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(new ChatMessage("user", prompt)))
                .temperature(0.2)   // low = deterministic output
                .maxTokens(2000)
                .build();

        String result = openAi.createChatCompletion(req)
                .getChoices().get(0).getMessage().getContent();
        log.info("Generated {} chars of test code for {}", result.length(), endpoint);
        return result;
    }
}