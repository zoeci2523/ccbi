package com.cicih.ccbi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.model.dto.chart.ChartAddRequest;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cicih.ccbi.model.vo.MQTaskResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ChartDetailService extends IService<ChartDetail> {

    @NotNull
    @Transactional(rollbackFor = Exception.class)
    String create(@NotNull ChartAddRequest chartAddRequest, @NotNull String userId);

    @NotNull
    boolean delete(@NotNull String contentId, @NotNull String userId);

    Page<ChartDetail> getChartByPage(@NotNull ChartQueryRequest chartQueryRequest);

    BaseResponse<MQTaskResponse> startChartGeneration(@NotNull MultipartFile multipartFile,
                                                      @NotNull ChartAddRequest addRequest,
                                                      @NotNull String userId);

    @Nullable
    ChartDetail getChartByTaskId(@NotNull String taskId);

    @NotNull
    ChartDetail updateGenChartResult(
        @NotNull String chartId,
        @NotNull String genChart,
        @NotNull String genResult
    );
}
