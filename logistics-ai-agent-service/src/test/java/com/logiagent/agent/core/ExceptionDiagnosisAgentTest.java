package com.logiagent.agent.core;

import com.logiagent.agent.dto.ChatResponse;
import com.logiagent.agent.tool.TrackTool;
import com.logiagent.agent.tool.WaybillTool;
import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.common.enums.TrackActionEnum;
import com.logiagent.common.enums.WaybillStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionDiagnosisAgentTest {

    @Mock
    private WaybillTool waybillTool;

    @Mock
    private TrackTool trackTool;

    @InjectMocks
    private ExceptionDiagnosisAgent agent;

    @Test
    void diagnoseWaybillExceptionWithLatestTrack() {
        String sessionId = "AGENT202605100001";
        String waybillNo = "WB20260509204042075";
        WaybillDTO waybill = new WaybillDTO();
        waybill.setWaybillNo(waybillNo);
        waybill.setCurrentStatus(WaybillStatusEnum.EXCEPTION.name());
        waybill.setExceptionType("TIMEOUT");
        waybill.setExceptionReason("No update for a long time");

        TrackDTO latest = new TrackDTO();
        latest.setWaybillNo(waybillNo);
        latest.setAction(TrackActionEnum.COLLECTED.name());
        latest.setDescription("Courier collected the parcel");
        latest.setEventTime(LocalDateTime.now().minusHours(30));

        when(waybillTool.getWaybillByNo(sessionId, waybillNo)).thenReturn(waybill);
        when(trackTool.getTracksByWaybillNo(sessionId, waybillNo)).thenReturn(List.of(latest));
        when(trackTool.getLatestTrackByWaybillNo(sessionId, waybillNo)).thenReturn(latest);

        ChatResponse response = agent.diagnose(sessionId, waybillNo);

        assertThat(response.getIntent()).isEqualTo("WAYBILL_EXCEPTION_DIAGNOSIS");
        assertThat(response.getAnswer()).contains("EXCEPTION", "TIMEOUT", "Courier collected the parcel");
        assertThat(response.getToolCalls()).contains(
                "WaybillTool.getWaybillByNo",
                "TrackTool.getTracksByWaybillNo",
                "TrackTool.getLatestTrackByWaybillNo"
        );
    }
}
