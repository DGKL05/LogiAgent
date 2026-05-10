package com.logiagent.api.client;

import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.request.CreateTrackRequest;
import com.logiagent.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "logistics-track-service", path = "/tracks")
public interface TrackFeignClient {

    @PostMapping
    Result<TrackDTO> createTrack(@RequestBody CreateTrackRequest request);

    @GetMapping("/waybill/{waybillNo}")
    Result<List<TrackDTO>> listByWaybillNo(@PathVariable("waybillNo") String waybillNo);

    @GetMapping("/waybill/{waybillNo}/latest")
    Result<TrackDTO> getLatestByWaybillNo(@PathVariable("waybillNo") String waybillNo);

    @GetMapping("/statistics/daily")
    Result<TrackDailyStatisticsDTO> dailyStatistics(@RequestParam("date") LocalDate date);

    @GetMapping("/statistics/waybill/{waybillNo}")
    Result<TrackDailyStatisticsDTO> waybillStatistics(@PathVariable("waybillNo") String waybillNo);
}
