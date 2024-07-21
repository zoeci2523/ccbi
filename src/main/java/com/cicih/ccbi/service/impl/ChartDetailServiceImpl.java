package com.cicih.ccbi.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.common.ResultUtils;
import com.cicih.ccbi.constant.FileConstant;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.mapper.TaskMapper;
import com.cicih.ccbi.model.dto.chart.ChartAddRequest;
import com.cicih.ccbi.model.dto.chart.ChartQueryRequest;
import com.cicih.ccbi.model.dto.task.TaskQueryRequest;
import com.cicih.ccbi.model.dto.task.TaskUpdateRequest;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.model.vo.ChartVO;
import com.cicih.ccbi.model.vo.MQTaskResponse;
import com.cicih.ccbi.mq.GenerateChartMsgProducer;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.mapper.ChartDetailMapper;
import com.cicih.ccbi.service.TaskService;
import com.cicih.ccbi.service.UserService;
import com.cicih.ccbi.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.retry.backoff.ThreadWaitSleeper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartDetailServiceImpl extends ServiceImpl<ChartDetailMapper, ChartDetail> implements ChartDetailService {


    @Resource
    UserService userService;
    @Resource
    TaskService taskService;
    @Resource
    ChartDetailMapper chartDetailMapper;
    @Resource
    TaskMapper taskMapper;
    @Resource
    GenerateChartMsgProducer chartMsgProducer;

    @Override
    @NotNull
    @Transactional(rollbackFor = Exception.class)
    public String create(
        @NotNull ChartAddRequest chartAddRequest,
        @NotNull String userId
    ) {
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
    public boolean delete(
        @NotNull String contentId,
        @NotNull String userId
    ) {
        ChartDetail chart = getById(contentId);
        ThrowUtils.throwIf(
            chart == null,
            new BusinessException(ErrorCode.DELETE_ERROR, "Failed to delete chart - chart not found")
        );
        User user = userService.getById(userId);
        ThrowUtils.throwIf(
            user == null,
            new BusinessException(ErrorCode.DELETE_ERROR, "Failed to delete chart = user not found")
        );
        if (!chart.getUserId().equals(userId) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return removeById(contentId);
    }

    @Override
    public Page<ChartVO> getChartByPage(@NotNull ChartQueryRequest request) {
        long current = request.getCurrent();
        long size = request.getPageSize();
        // limit the maximum size for getting a page
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        if (request.getUserId() != null) {
            ThrowUtils.throwIf(userService.getById(request.getUserId()) == null, ErrorCode.NOT_FOUND_ERROR);
        }
        QueryWrapper<ChartDetail> queryWrapper = chartDetailMapper.getQueryWrapper(request);
        List<ChartDetail> chartList = chartDetailMapper.selectList(queryWrapper);
        Page<ChartVO> chartVOPage = new Page<>(current, size);
        List<ChartVO> chartVOList = new ArrayList<>();
        if (!chartList.isEmpty()) {
            chartList.stream().peek(
                chart -> {
                    ChartVO chartVO = new ChartVO();
                    BeanUtils.copyProperties(chart, chartVO);
                    Task task = taskService.getTaskByQueryParams(TaskQueryRequest.builder().contentId(chart.getId()).build());
                    try {
                        chartVO.setStatus(Task.Status.from(task.getStatus()).getValue());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    chartVOList.add(chartVO);
                }
            ).collect(Collectors.toList());
        }
        chartVOPage.setRecords(chartVOList);
        return chartVOPage;
    }

    @Override
    public BaseResponse<MQTaskResponse> startChartGeneration(
        @NotNull MultipartFile multipartFile,
        @NotNull ChartAddRequest addRequest,
        @NotNull String userId
    ) {
        // validate file
        String title = addRequest.getTitle();
        ThrowUtils.throwIf(StringUtils.isNotBlank(title) && title.length() > 100,
            ErrorCode.PARAMS_ERROR,
            "The length of chart title cannot be more than 100"
        );
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > FileConstant.UPLOAD_FILE_SIZE_LIMITATION,
            ErrorCode.PARAMS_ERROR,
            "Upload file cannot be over 1MB"
        );
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = "." + FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!FileConstant.VALID_FILE_SUFFIX_LIST.contains(suffix),
            ErrorCode.PARAMS_ERROR,
            "Invalid file suffix"
        );

        // validate user qualification
        User user = userService.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.CREATE_ERROR, "User not found");
        // TODO limit maximum number of chart can generate based on user's level
        //        ThrowUtils.throwIf(levelService.getTaskLimit(user.getLevelId()), ErrorCode.CREATE_ERROR,
        //                "Fail to generate chart since the number of running tasks exceed the limit on current level.");

        // TODO save raw file to S3 (or other) in the future
        // save chart and send message
        addRequest.setChartData(ExcelUtils.fileToString(multipartFile, suffix));
        String chartId = create(addRequest, userId);
        TaskQueryRequest queryRequest =
            TaskQueryRequest.builder()
                            .userId(userId)
                            .contentId(chartId)
                            .build();
        ThrowUtils.throwIf(!queryRequest.validParams(), ErrorCode.CREATE_ERROR, "Invalid task query param");
        Task task = taskService.getTaskByQueryParams(queryRequest);
        chartMsgProducer.sendMessage(task.getId());

        MQTaskResponse mqTaskResponse = new MQTaskResponse();
        mqTaskResponse.setTaskId(task.getId());
        // update task status if message send out successfully
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setId(task.getId());
        updateRequest.setStatus(Task.Status.WAITING);
        if (!taskService.update(taskMapper.getUpdateWrapper(updateRequest))) {
            return ResultUtils.error(ErrorCode.CREATE_ERROR, "Failed to generate chart");
        }
        return ResultUtils.success(mqTaskResponse);
    }

    @Override
    @Nullable
    public ChartDetail getChartByTaskId(@NotNull String taskId) {
        // todo 为数据库里的 taskId 加一个 index 和 unique 约束
        QueryWrapper<ChartDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId);
        return chartDetailMapper.selectOne(wrapper);
    }

    @Override
    @NotNull
    public boolean updateGenChartResult(
        @NotNull String chartId,
        @NotNull String genChart,
        @NotNull String genResult
    ) {
        ChartDetail updateChartResult = new ChartDetail();
        updateChartResult.setId(chartId);
        updateChartResult.setGenerateChart(genChart);
        updateChartResult.setGenerateResult(genResult);
        return updateById(updateChartResult);
    }


}





