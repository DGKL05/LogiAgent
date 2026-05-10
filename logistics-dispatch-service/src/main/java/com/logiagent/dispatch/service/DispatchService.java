package com.logiagent.dispatch.service;

import com.logiagent.api.dto.DispatchSuggestionDTO;
import com.logiagent.api.dto.DispatchTaskDTO;
import com.logiagent.api.dto.StationLoadDTO;
import com.logiagent.api.request.CreateDispatchTaskRequest;

import java.util.List;

public interface DispatchService {

    DispatchTaskDTO createTask(CreateDispatchTaskRequest request);

    DispatchTaskDTO getTask(String taskNo);

    StationLoadDTO getStationLoad(Long stationId);

    List<StationLoadDTO> loadRanking();

    List<DispatchSuggestionDTO> suggestions();
}
