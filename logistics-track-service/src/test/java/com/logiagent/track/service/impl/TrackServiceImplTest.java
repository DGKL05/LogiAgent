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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

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
}
