package com.logiagent.agent.service;

import com.logiagent.api.dto.AgentToolLogDTO;
import com.logiagent.api.dto.AgentUsageStatisticsDTO;
import com.logiagent.api.dto.DashboardOverviewDTO;
import com.logiagent.api.dto.DashboardRiskDTO;
import com.logiagent.api.dto.DashboardTrendDTO;
import com.logiagent.api.request.AdminAgentSessionQueryRequest;
import com.logiagent.common.result.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface AdminAgentService {

    PageResult<com.logiagent.api.dto.AgentSessionDTO> pageSessions(AdminAgentSessionQueryRequest request);

    com.logiagent.api.dto.AgentSessionDTO getSession(String sessionId);

    List<AgentToolLogDTO> toolLogs(String sessionId);

    AgentUsageStatisticsDTO statistics();

    DashboardOverviewDTO dashboardOverview();

    List<DashboardTrendDTO> dashboardTrends(LocalDate startDate, LocalDate endDate);

    DashboardRiskDTO dashboardRisks();
}
