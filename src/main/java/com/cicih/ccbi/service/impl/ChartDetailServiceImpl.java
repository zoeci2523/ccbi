package com.cicih.ccbi.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.model.dto.chart.ChartAddRequest;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.mapper.ChartDetailMapper;
import com.cicih.ccbi.service.TaskService;
import com.cicih.ccbi.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ChartDetailServiceImpl extends ServiceImpl<ChartDetailMapper, ChartDetail>
        implements ChartDetailService {

    @Resource
    UserService userService;
    @Resource
    TaskService taskService;
    @Resource
    ChartDetailMapper chartDetailMapper;

    @Override
    @NotNull
    @Transactional(rollbackFor = Exception.class)
    public String create(@NotNull ChartAddRequest chartAddRequest, @NotNull String userId) {
        ThrowUtils.throwIf(!chartAddRequest.validParams(), ErrorCode.PARAMS_ERROR);
        // initialize task
        String chartId = "content-" + UUID.fastUUID();
        String taskId = taskService.iniTask(userId, chartId, Task.Type.CHART);
        // create chart
        ChartDetail chart = new ChartDetail();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chart.setId(chartId);
        chart.setUserId(userId);
        chart.setTaskId(taskId);
        if (!save(chart)) {
            throw new BusinessException(ErrorCode.CREATE_ERROR, "Failed to create chart");
        }
        return getById(chartId).getId();
    }

    @Override
    @NotNull
    public boolean delete(@NotNull String contentId, @NotNull String userId) {
        ChartDetail chart = getById(contentId);
        ThrowUtils.throwIf(chart == null, new BusinessException(ErrorCode.DELETE_ERROR,
                "Failed to delete chart - chart not found"));
        User user = userService.getById(userId);
        ThrowUtils.throwIf(user == null, new BusinessException(ErrorCode.DELETE_ERROR,
                "Failed to delete chart = user not found"));
        if (!chart.getUserId().equals(userId) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return removeById(contentId);
    }

    @Override
    public Page<ChartDetail> getChartByPage(@NotNull ChartQueryRequest request) {
        long current = request.getCurrent();
        long size = request.getPageSize();
        // limit the maximum size for getting a page
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        if (request.getUserId() != null){
            ThrowUtils.throwIf(userService.getById(request.getUserId()) == null, ErrorCode.NOT_FOUND_ERROR);
        }
        return page(new Page<>(current, size),
                chartDetailMapper.getQueryWrapper(request));
    }
}





