package com.logiagent.track.service;

import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.request.CreateTrackRequest;

import java.time.LocalDate;
import java.util.List;

public interface TrackService {

    TrackDTO createTrack(CreateTrackRequest request);

    List<TrackDTO> listByWaybillNo(String waybillNo);

    TrackDTO getLatestByWaybillNo(String waybillNo);

    TrackDailyStatisticsDTO dailyStatistics(LocalDate date);

    TrackDailyStatisticsDTO waybillStatistics(String waybillNo);
}
