package com.cicih.ccbi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cicih.ccbi.constant.CommonConstant;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.dto.task.TaskQueryRequest;
import com.cicih.ccbi.model.dto.task.TaskUpdateRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cicih.ccbi.utils.SqlUtils;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public interface TaskMapper extends BaseMapper<Task> {

    default QueryWrapper<Task> getQueryWrapper(@NotNull TaskQueryRequest taskQueryRequest) {

        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        String userId = taskQueryRequest.getUserId();
        String contentId = taskQueryRequest.getContentId();
        Integer type = taskQueryRequest.getType() == null ? null : taskQueryRequest.getType().getCode();
        Integer status = taskQueryRequest.getStatus() == null ? null : taskQueryRequest.getStatus().getCode();

        queryWrapper.eq(StringUtils.isNotBlank(userId), "userId", userId);
        queryWrapper.eq(StringUtils.isNotBlank(contentId), "contentId", contentId);
        queryWrapper.eq(type != null, "type", type);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq("isDelete", false);
        return queryWrapper;
    }

    default UpdateWrapper<Task> getUpdateWrapper(@NotNull TaskUpdateRequest taskUpdateRequest){
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        String id = taskUpdateRequest.getId();
        Integer status = taskUpdateRequest.getStatus() == null ? null : taskUpdateRequest.getStatus().getCode();

        updateWrapper.eq( "id", id);
        updateWrapper.set(status != null, "status", status);
        updateWrapper.setSql("updatedTime = NOW()");
        return updateWrapper;
    }

}





