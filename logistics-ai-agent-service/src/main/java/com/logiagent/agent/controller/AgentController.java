package com.logiagent.agent.controller;

import com.logiagent.agent.dto.AgentSessionDTO;
import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.request.ChatRequest;
import com.logiagent.agent.service.AgentChatService;
import com.logiagent.agent.service.AgentSessionService;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentChatService agentChatService;
    private final AgentSessionService agentSessionService;

    public AgentController(AgentChatService agentChatService, AgentSessionService agentSessionService) {
        this.agentChatService = agentChatService;
        this.agentSessionService = agentSessionService;
    }

    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        return Result.success(agentChatService.chat(request));
    }

    @GetMapping("/sessions/{sessionId}")
    public Result<AgentSessionDTO> getSession(@PathVariable("sessionId") String sessionId) {
        return Result.success(agentSessionService.getBySessionId(sessionId));
    }

    @GetMapping("/sessions")
    public Result<PageResult<AgentSessionDTO>> pageSessions(@RequestParam(name = "page", defaultValue = "1") long page,
                                                            @RequestParam(name = "size", defaultValue = "10") long size) {
        return Result.success(agentSessionService.pageSessions(page, size));
    }
}
