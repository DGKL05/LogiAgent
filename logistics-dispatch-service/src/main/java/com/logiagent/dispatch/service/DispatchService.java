package com.logiagent.dispatch.service;

import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.AdminDispatchTaskQueryRequest;
import com.logiagent.api.request.CreateDispatchTaskRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.result.PageResult;

import java.util.List;

public interface DispatchService {

    DispatchTaskDTO createTask(CreateDispatchTaskRequest request);

    DispatchTaskDTO getTask(String taskNo);

    PageResult<DispatchTaskDTO> pageAdminTasks(AdminDispatchTaskQueryRequest request);

    DispatchTaskDTO updateTaskStatus(String taskNo, UpdateStatusRequest request);

    StationLoadDTO getStationLoad(Long stationId);

    List<StationLoadDTO> loadRanking();

    List<DispatchSuggestionDTO> suggestions();
}
