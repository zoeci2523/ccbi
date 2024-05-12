package com.cicih.ccbi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cicih.ccbi.annotation.AuthCheck;
import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.common.DeleteRequest;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.common.ResultUtils;
import com.cicih.ccbi.constant.UserConstant;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.mapper.ChartDetailMapper;
import com.cicih.ccbi.model.dto.chart.*;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.model.vo.ChartResponse;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.service.UserService;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Chart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.cicih.ccbi.common.ErrorCode.OPERATION_ERROR;

@RestController
@RequestMapping("/chart")
public class ChartDetailController {
    @Resource
    UserService userService;
    @Resource
    ChartDetailService chartDetailService;
    @Resource
    ChartDetailMapper chartDetailMapper;

    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody @NotNull ChartAddRequest chartAddRequest,
                                       @NotNull HttpServletRequest request) {
        ChartDetail chart = new ChartDetail();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        if (!chartDetailService.save(chart)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(chart.getId());

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody @NotNull DeleteRequest deleteRequest,
                                             @NotNull HttpServletRequest request) {
        ChartDetail chart = chartDetailService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(chart == null, ErrorCode.NOT_FOUND_ERROR);
        User user = userService.getLoginUser(request);
        if (!chart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(chartDetailService.removeById(deleteRequest.getId()));
    }

    /**
     * 更新图表信息（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> updateChart(@RequestBody @NotNull ChartUpdateRequest chartUpdateRequest) {
        ThrowUtils.throwIf(chartDetailService.getById(chartUpdateRequest.getId()) == null,
                ErrorCode.NOT_FOUND_ERROR);
        ChartDetail updatedChart = new ChartDetail();
        BeanUtils.copyProperties(chartUpdateRequest, updatedChart);
        if (!chartDetailService.updateById(updatedChart)) {
            throw new BusinessException(ErrorCode.UPDATED_ERROR);
        }
        return ResultUtils.success(updatedChart.getId());
    }


    @GetMapping("/get")
    public BaseResponse<ChartDetail> getChartById(@NotNull long id) {
        ChartDetail chart = chartDetailService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<ChartDetail>> listChartByPage(@RequestBody @NotNull ChartQueryRequest chartQueryRequest,
                                                           @Nullable HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        if (request != null) {
            User loginUser = userService.getLoginUser(request);
            chartQueryRequest.setUserId(loginUser.getId());
        }
        // limit the maximum size for getting a page
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ChartDetail> chartPage = chartDetailService.page(new Page<>(current, size),
                chartDetailMapper.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    @PostMapping("/edit")
    public BaseResponse<Long> editChart(@RequestBody @NotNull ChartEditRequest chartEditRequest,
                                        @NotNull HttpServletRequest request) {
        ChartDetail oldChart = chartDetailService.getById(chartEditRequest.getId());
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        ChartDetail newChart = new ChartDetail();
        BeanUtils.copyProperties(chartEditRequest, newChart);
        return chartDetailService.updateById(newChart)? ResultUtils.success(newChart.getId()) :
                ResultUtils.error(ErrorCode.UPDATED_ERROR, "Failed to edit chart: " + oldChart.getId());
    }

    @PostMapping("/generate/chart")
    public BaseResponse<ChartResponse> generateChart(@RequestPart("file") MultipartFile multipartFile,
                                                     @NotNull GenerateChartRequest generateChartRequest,
                                                     @NotNull HttpServletRequest request) {
        // TODO 待补充
        return null;
    }


}
