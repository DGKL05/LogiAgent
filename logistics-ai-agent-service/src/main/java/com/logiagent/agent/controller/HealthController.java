package com.logiagent.agent.controller;

import com.logiagent.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/internal/health")
    public Result<String> health() {
        return Result.success("logistics-ai-agent-service");
    }
}
