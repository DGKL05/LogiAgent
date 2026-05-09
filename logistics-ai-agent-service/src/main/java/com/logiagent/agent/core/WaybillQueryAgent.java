package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.TrackTool;
import com.logiagent.agent.tool.WaybillTool;
import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.WaybillDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaybillQueryAgent {

    private final WaybillTool waybillTool;
    private final TrackTool trackTool;

    public WaybillQueryAgent(WaybillTool waybillTool, TrackTool trackTool) {
        this.waybillTool = waybillTool;
        this.trackTool = trackTool;
    }

    public ChatResponse query(String sessionId, String waybillNo) {
        WaybillDTO waybill = waybillTool.getWaybillByNo(sessionId, waybillNo);
        TrackDTO latestTrack = trackTool.getLatestTrackByWaybillNo(sessionId, waybillNo);

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.WAYBILL_QUERY.name());
        response.setAnswer("运单 " + waybillNo + " 当前状态为 " + waybill.getCurrentStatus()
                + "，最新轨迹为「" + latestTrack.getDescription() + "」。");
        response.setToolCalls(List.of(
                "WaybillTool.getWaybillByNo",
                "TrackTool.getLatestTrackByWaybillNo"
        ));
        return response;
    }
}
