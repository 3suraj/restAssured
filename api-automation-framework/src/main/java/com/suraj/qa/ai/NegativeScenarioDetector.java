package com.suraj.qa.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import java.util.List;

public class NegativeScenarioDetector {

    private final OpenAiService openAi;
    private final ObjectMapper mapper = new ObjectMapper();

    public NegativeScenarioDetector() {
        this.openAi = new OpenAiService(System.getenv("OPENAI_API_KEY"));
    }

    public String detectGaps(String existingTestCode, String openApiSpec) {
        String prompt = String.format("""
            You are a Principal QA Engineer reviewing test coverage.
            EXISTING TESTS: %s
            OPENAPI SPEC: %s
            Find ALL missing negative test scenarios.
            For each gap return JSON array of:
            { endpoint, missingScenario, suggestedTestName,
              expectedStatusCode, riskLevel: HIGH|MEDIUM|LOW }
            Output ONLY a JSON array. No explanation.""",
                existingTestCode, openApiSpec);

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(new ChatMessage("user", prompt)))
                .temperature(0.1)
                .maxTokens(2000)
                .build();

        return openAi.createChatCompletion(req)
                .getChoices().get(0).getMessage().getContent();
    }
}

// Wire into a JUnit test to fail CI on HIGH risk gaps:
// String gaps = new NegativeScenarioDetector().detectGaps(readTests(), readSpec());
// assertThat(gaps).doesNotContain("\"riskLevel\":\"HIGH\"");
