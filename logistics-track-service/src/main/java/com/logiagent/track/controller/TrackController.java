package com.logiagent.track.controller;

import com.logiagent.api.dto.TrackDTO;
import com.logiagent.api.dto.TrackDailyStatisticsDTO;
import com.logiagent.api.request.CreateTrackRequest;
import com.logiagent.common.result.Result;
import com.logiagent.track.service.TrackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping
    public Result<TrackDTO> createTrack(@RequestBody CreateTrackRequest request) {
        return Result.success(trackService.createTrack(request));
    }

    @GetMapping("/waybill/{waybillNo}")
    public Result<List<TrackDTO>> listByWaybillNo(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(trackService.listByWaybillNo(waybillNo));
    }

    @GetMapping("/waybill/{waybillNo}/latest")
    public Result<TrackDTO> getLatestByWaybillNo(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(trackService.getLatestByWaybillNo(waybillNo));
    }

    @GetMapping("/statistics/daily")
    public Result<TrackDailyStatisticsDTO> dailyStatistics(@RequestParam("date") LocalDate date) {
        return Result.success(trackService.dailyStatistics(date));
    }

    @GetMapping("/statistics/waybill/{waybillNo}")
    public Result<TrackDailyStatisticsDTO> waybillStatistics(@PathVariable("waybillNo") String waybillNo) {
        return Result.success(trackService.waybillStatistics(waybillNo));
    }
}
