package com.cicih.ccbi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cicih.ccbi.model.dto.chart.ChartAddRequest;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

public interface ChartDetailService extends IService<ChartDetail> {

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    String create(@NotNull ChartAddRequest chartAddRequest, @NotNull String userId);

    @NotNull
    boolean delete(@NotNull String contentId, @NotNull String userId);

    Page<ChartDetail> getChartByPage(@NotNull ChartQueryRequest chartQueryRequest);
}
