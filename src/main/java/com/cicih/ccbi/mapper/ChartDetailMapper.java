package com.cicih.ccbi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cicih.ccbi.constant.CommonConstant;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cicih.ccbi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public interface ChartDetailMapper extends BaseMapper<ChartDetail> {

    default QueryWrapper<ChartDetail> getQueryWrapper(@NotNull ChartQueryRequest chartQueryRequest) {
        QueryWrapper<ChartDetail> queryWrapper = new QueryWrapper<>();
        String title = chartQueryRequest.getTitle();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        String userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        Boolean publicOnly = chartQueryRequest.getPublicOnly();

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
//        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(publicOnly != null && publicOnly, "isPublic", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




