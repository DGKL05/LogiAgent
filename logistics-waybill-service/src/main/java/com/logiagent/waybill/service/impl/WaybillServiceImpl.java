package com.logiagent.waybill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logiagent.api.dto.WaybillDTO;
import com.logiagent.api.request.CreateWaybillRequest;
import com.logiagent.api.request.MarkExceptionRequest;
import com.logiagent.api.request.UpdateStatusRequest;
import com.logiagent.common.enums.ExceptionTypeEnum;
import com.logiagent.common.enums.WaybillStatusEnum;
import com.logiagent.common.exception.BusinessException;
import com.logiagent.common.result.ErrorCode;
import com.logiagent.common.result.PageResult;
import com.logiagent.waybill.entity.WaybillEntity;
import com.logiagent.waybill.mapper.WaybillMapper;
import com.logiagent.waybill.service.WaybillService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WaybillServiceImpl implements WaybillService {

    private static final DateTimeFormatter NUMBER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final WaybillMapper waybillMapper;

    public WaybillServiceImpl(WaybillMapper waybillMapper) {
        this.waybillMapper = waybillMapper;
    }

    @Override
    public WaybillDTO createWaybill(CreateWaybillRequest request) {
        if (request == null || !StringUtils.hasText(request.getOrderNo())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "orderNo is required");
        }
        WaybillEntity existing = waybillMapper.selectOne(
                new LambdaQueryWrapper<WaybillEntity>().eq(WaybillEntity::getOrderNo, request.getOrderNo())
        );
        if (existing != null) {
            return toDTO(existing);
        }

        LocalDateTime now = LocalDateTime.now();
        WaybillEntity waybill = new WaybillEntity();
        waybill.setWaybillNo(generateWaybillNo(now));
        waybill.setOrderNo(request.getOrderNo());
        waybill.setCurrentStatus(WaybillStatusEnum.CREATED.name());
        waybill.setCreateTime(now);
        waybill.setUpdateTime(now);
        waybillMapper.insert(waybill);
        return toDTO(waybill);
    }

    @Override
    public WaybillDTO getByWaybillNo(String waybillNo) {
        return toDTO(selectByWaybillNo(waybillNo));
    }

    @Override
    public WaybillDTO getByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "orderNo is required");
        }
        WaybillEntity waybill = waybillMapper.selectOne(
                new LambdaQueryWrapper<WaybillEntity>().eq(WaybillEntity::getOrderNo, orderNo)
        );
        if (waybill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "waybill not found");
        }
        return toDTO(waybill);
    }

    @Override
    public WaybillDTO updateStatus(String waybillNo, UpdateStatusRequest request) {
        if (request == null || !StringUtils.hasText(request.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "status is required");
        }
        WaybillStatusEnum.valueOf(request.getStatus());
        WaybillEntity waybill = selectByWaybillNo(waybillNo);
        waybill.setCurrentStatus(request.getStatus());
        waybill.setUpdateTime(LocalDateTime.now());
        waybillMapper.updateById(waybill);
        return toDTO(waybill);
    }

    @Override
    public WaybillDTO markException(String waybillNo, MarkExceptionRequest request) {
        if (request == null || !StringUtils.hasText(request.getExceptionType())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "exceptionType is required");
        }
        ExceptionTypeEnum.valueOf(request.getExceptionType());
        WaybillEntity waybill = selectByWaybillNo(waybillNo);
        waybill.setCurrentStatus(WaybillStatusEnum.EXCEPTION.name());
        waybill.setExceptionType(request.getExceptionType());
        waybill.setExceptionReason(request.getExceptionReason());
        waybill.setUpdateTime(LocalDateTime.now());
        waybillMapper.updateById(waybill);
        return toDTO(waybill);
    }

    @Override
    public PageResult<WaybillDTO> pageExceptions(long page, long size) {
        Page<WaybillEntity> pageParam = new Page<>(Math.max(page, 1), Math.max(size, 1));
        Page<WaybillEntity> waybillPage = waybillMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<WaybillEntity>()
                        .eq(WaybillEntity::getCurrentStatus, WaybillStatusEnum.EXCEPTION.name())
                        .orderByDesc(WaybillEntity::getUpdateTime)
        );
        List<WaybillDTO> records = waybillPage.getRecords().stream().map(this::toDTO).toList();
        return PageResult.of(records, waybillPage.getTotal(), waybillPage.getCurrent(), waybillPage.getSize());
    }

    private WaybillEntity selectByWaybillNo(String waybillNo) {
        if (!StringUtils.hasText(waybillNo)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "waybillNo is required");
        }
        WaybillEntity waybill = waybillMapper.selectOne(
                new LambdaQueryWrapper<WaybillEntity>().eq(WaybillEntity::getWaybillNo, waybillNo)
        );
        if (waybill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "waybill not found");
        }
        return waybill;
    }

    private String generateWaybillNo(LocalDateTime now) {
        return "WB" + now.format(NUMBER_TIME_FORMATTER);
    }

    private WaybillDTO toDTO(WaybillEntity waybill) {
        WaybillDTO dto = new WaybillDTO();
        dto.setId(waybill.getId());
        dto.setWaybillNo(waybill.getWaybillNo());
        dto.setOrderNo(waybill.getOrderNo());
        dto.setCurrentStatus(waybill.getCurrentStatus());
        dto.setExceptionType(waybill.getExceptionType());
        dto.setExceptionReason(waybill.getExceptionReason());
        dto.setCreateTime(waybill.getCreateTime());
        dto.setUpdateTime(waybill.getUpdateTime());
        return dto;
    }
}
