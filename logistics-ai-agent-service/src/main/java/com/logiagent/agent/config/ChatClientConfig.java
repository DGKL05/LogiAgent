package com.logiagent.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final String openAiApiKey;

    public ChatClientConfig(@Value("${spring.ai.openai.api-key:}") String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }
}
