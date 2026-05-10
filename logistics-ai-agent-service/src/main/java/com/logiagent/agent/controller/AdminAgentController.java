package com.logiagent.agent.controller;

import com.logiagent.agent.service.AdminAgentService;
import com.logiagent.api.dto.AgentToolLogDTO;
import com.logiagent.api.dto.AgentUsageStatisticsDTO;
import com.logiagent.api.dto.DashboardOverviewDTO;
import com.logiagent.api.dto.DashboardRiskDTO;
import com.logiagent.api.dto.DashboardTrendDTO;
import com.logiagent.api.request.AdminAgentSessionQueryRequest;
import com.logiagent.common.result.PageResult;
import com.logiagent.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class AdminAgentController {

    private final AdminAgentService adminAgentService;

    public AdminAgentController(AdminAgentService adminAgentService) {
        this.adminAgentService = adminAgentService;
    }

    @GetMapping("/admin/agent/sessions")
    public Result<PageResult<com.logiagent.api.dto.AgentSessionDTO>> pageSessions(@ModelAttribute AdminAgentSessionQueryRequest request) {
        return Result.success(adminAgentService.pageSessions(request));
    }

    @GetMapping("/admin/agent/sessions/{sessionId}")
    public Result<com.logiagent.api.dto.AgentSessionDTO> getSession(@PathVariable("sessionId") String sessionId) {
        return Result.success(adminAgentService.getSession(sessionId));
    }

    @GetMapping("/admin/agent/sessions/{sessionId}/tool-logs")
    public Result<List<AgentToolLogDTO>> toolLogs(@PathVariable("sessionId") String sessionId) {
        return Result.success(adminAgentService.toolLogs(sessionId));
    }

    @GetMapping("/admin/agent/statistics")
    public Result<AgentUsageStatisticsDTO> statistics() {
        return Result.success(adminAgentService.statistics());
    }

    @GetMapping("/admin/dashboard/overview")
    public Result<DashboardOverviewDTO> dashboardOverview() {
        return Result.success(adminAgentService.dashboardOverview());
    }

    @GetMapping("/admin/dashboard/trends")
    public Result<List<DashboardTrendDTO>> dashboardTrends(@RequestParam(name = "startDate", required = false) LocalDate startDate,
                                                           @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        return Result.success(adminAgentService.dashboardTrends(startDate, endDate));
    }

    @GetMapping("/admin/dashboard/risks")
    public Result<DashboardRiskDTO> dashboardRisks() {
        return Result.success(adminAgentService.dashboardRisks());
    }
}
