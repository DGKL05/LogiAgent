package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.enums.AgentIntent;
import com.logiagent.agent.tool.TrackTool;
import com.logiagent.agent.tool.WaybillTool;
import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.common.enums.WaybillStatusEnum;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionDiagnosisAgent {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WaybillTool waybillTool;
    private final TrackTool trackTool;

    public ExceptionDiagnosisAgent(WaybillTool waybillTool, TrackTool trackTool) {
        this.waybillTool = waybillTool;
        this.trackTool = trackTool;
    }

    public ChatResponse diagnose(String sessionId, String waybillNo) {
        WaybillDTO waybill = waybillTool.getWaybillByNo(sessionId, waybillNo);
        List<TrackDTO> tracks = trackTool.getTracksByWaybillNo(sessionId, waybillNo);
        TrackDTO latestTrack = trackTool.getLatestTrackByWaybillNo(sessionId, waybillNo);

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setIntent(AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS.name());
        response.setAnswer(buildAnswer(waybill, tracks, latestTrack));
        response.setToolCalls(List.of(
                "WaybillTool.getWaybillByNo",
                "TrackTool.getTracksByWaybillNo",
                "TrackTool.getLatestTrackByWaybillNo"
        ));
        return response;
    }

    private String buildAnswer(WaybillDTO waybill, List<TrackDTO> tracks, TrackDTO latestTrack) {
        List<String> reasons = new ArrayList<>();
        if (WaybillStatusEnum.EXCEPTION.name().equals(waybill.getCurrentStatus())) {
            reasons.add("运单已被标记为异常");
        }
        if (waybill.getExceptionType() != null) {
            reasons.add("异常类型为 " + waybill.getExceptionType());
        }
        if (latestTrack.getEventTime() != null) {
            long hours = Duration.between(latestTrack.getEventTime(), LocalDateTime.now()).toHours();
            if (hours >= 24) {
                reasons.add("最近轨迹距今约 " + hours + " 小时，存在长时间未更新风险");
            }
        }
        if (reasons.isEmpty()) {
            reasons.add("当前未发现明确异常，建议继续关注后续轨迹");
        }

        String eventTime = latestTrack.getEventTime() == null
                ? "未知时间"
                : latestTrack.getEventTime().format(DATE_TIME_FORMATTER);
        String trackDescription = latestTrack.getDescription() == null ? latestTrack.getAction() : latestTrack.getDescription();
        String exceptionReason = waybill.getExceptionReason() == null ? "暂无异常原因备注" : waybill.getExceptionReason();

        return "该运单当前状态为 " + waybill.getCurrentStatus()
                + "，异常备注为：" + exceptionReason
                + "。最近一次轨迹为 " + eventTime + " 的「" + trackDescription + "」。"
                + "系统判断可能原因是：" + String.join("；", reasons)
                + "。目前共查询到 " + tracks.size() + " 条轨迹，建议联系当前处理网点核实并补充最新扫描记录。";
    }
}
