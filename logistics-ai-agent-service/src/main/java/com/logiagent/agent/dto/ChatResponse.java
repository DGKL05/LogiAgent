package com.logiagent.agent.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatResponse implements Serializable {

    private String sessionId;
    private String intent;
    private String answer;
    private List<String> toolCalls = new ArrayList<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<String> toolCalls) {
        this.toolCalls = toolCalls;
    }
}
