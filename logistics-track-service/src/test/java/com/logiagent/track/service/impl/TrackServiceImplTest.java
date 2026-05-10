package com.logiagent.track.service.impl;

import com.logiagent.api.request.CreateTrackRequest;
import com.logiagent.common.enums.TrackActionEnum;
import com.logiagent.track.entity.TrackEntity;
import com.logiagent.track.mapper.TrackMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplTest {

    @Mock
    private TrackMapper trackMapper;

    @InjectMocks
    private TrackServiceImpl trackService;

    @Test
    void createTrackInsertsTrackWithDefaultEventTime() {
        CreateTrackRequest request = new CreateTrackRequest();
        request.setWaybillNo("WB202605090001");
        request.setAction(TrackActionEnum.COLLECTED.name());
        request.setDescription("Courier collected the parcel");
        request.setOperatorId(10L);

        var response = trackService.createTrack(request);

        ArgumentCaptor<TrackEntity> captor = ArgumentCaptor.forClass(TrackEntity.class);
        verify(trackMapper).insert(captor.capture());
        TrackEntity savedTrack = captor.getValue();
        assertThat(savedTrack.getWaybillNo()).isEqualTo("WB202605090001");
        assertThat(savedTrack.getAction()).isEqualTo(TrackActionEnum.COLLECTED.name());
        assertThat(savedTrack.getEventTime()).isNotNull();
        assertThat(response.getWaybillNo()).isEqualTo("WB202605090001");
    }

    @Test
    void dailyStatisticsUsesEventTimeForRealTrackCounts() {
        TrackEntity first = new TrackEntity();
        first.setWaybillNo("WB1");
        first.setAction(TrackActionEnum.COLLECTED.name());
        first.setEventTime(LocalDateTime.of(2026, 5, 10, 10, 0));
        TrackEntity second = new TrackEntity();
        second.setWaybillNo("WB2");
        second.setAction(TrackActionEnum.SIGNED.name());
        second.setEventTime(LocalDateTime.of(2026, 5, 10, 11, 0));
        TrackEntity third = new TrackEntity();
        third.setWaybillNo("WB1");
        third.setAction(TrackActionEnum.COLLECTED.name());
        third.setEventTime(LocalDateTime.of(2026, 5, 10, 12, 0));
        when(trackMapper.selectList(any())).thenReturn(List.of(first, second, third));

        var statistics = trackService.dailyStatistics(LocalDate.of(2026, 5, 10));

        assertThat(statistics.getDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(statistics.getTrackUpdateCount()).isEqualTo(3L);
        assertThat(statistics.getActiveWaybillCount()).isEqualTo(2L);
        assertThat(statistics.getActionCountMap()).containsAllEntriesOf(Map.of(
                TrackActionEnum.COLLECTED.name(), 2L,
                TrackActionEnum.SIGNED.name(), 1L
        ));
        assertThat(statistics.getLatestTrackUpdateTime()).isEqualTo(LocalDateTime.of(2026, 5, 10, 12, 0));
    }

    @Test
    void dailyStatisticsFallsBackToCreateTimeWhenEventTimeIsNull() {
        TrackEntity track = new TrackEntity();
        track.setWaybillNo("WB1");
        track.setAction(TrackActionEnum.CREATED.name());
        track.setCreateTime(LocalDateTime.of(2026, 5, 10, 9, 0));
        when(trackMapper.selectList(any())).thenReturn(List.of(track));

        var statistics = trackService.dailyStatistics(LocalDate.of(2026, 5, 10));

        assertThat(statistics.getTrackUpdateCount()).isEqualTo(1L);
        assertThat(statistics.getLatestTrackUpdateTime()).isEqualTo(LocalDateTime.of(2026, 5, 10, 9, 0));
    }
}
