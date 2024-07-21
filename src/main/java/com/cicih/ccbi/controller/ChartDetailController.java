package com.cicih.ccbi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cicih.ccbi.annotation.AuthCheck;
import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.common.ResultUtils;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.exception.ThrowUtils;
import com.cicih.ccbi.model.dto.chart.*;
import com.cicih.ccbi.model.entity.ChartDetail;
import com.cicih.ccbi.model.entity.User;
import com.cicih.ccbi.model.vo.ChartVO;
import com.cicih.ccbi.model.vo.MQTaskResponse;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/chart")
public class ChartDetailController {
    @Resource
    UserService userService;
    @Resource
    ChartDetailService chartDetailService;

    @PostMapping("/add")
    public BaseResponse<String> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        String chartId = chartDetailService.create(chartAddRequest, loginUser.getId());
        return ResultUtils.success(chartId);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@NotNull String chartId,
                                             @NotNull HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(chartDetailService.delete(chartId, user.getId()));
    }

    /**
     * update chart - admin ONLY
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = User.Role.ADMIN)
    public BaseResponse<String> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        ThrowUtils.throwIf(chartDetailService.getById(chartUpdateRequest.getId()) == null,
                ErrorCode.NOT_FOUND_ERROR);
        ChartDetail updatedChart = new ChartDetail();
        BeanUtils.copyProperties(chartUpdateRequest, updatedChart);
        if (!chartDetailService.updateById(updatedChart)) {
            throw new BusinessException(ErrorCode.UPDATE_ERROR);
        }
        return ResultUtils.success(updatedChart.getId());
    }


    /**
     * Get single chart
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ChartDetail> getChartById(@NotNull String id) {
        ChartDetail chart = chartDetailService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * Get my list of chart
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<ChartVO>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                         HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        return ResultUtils.success(chartDetailService.getChartByPage(chartQueryRequest));
    }

    /**
     * Get public chart by page
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<ChartVO>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        return ResultUtils.success(chartDetailService.getChartByPage(chartQueryRequest));
    }

    @PostMapping("/edit")
    public BaseResponse<String> editChart(@RequestBody ChartEditRequest chartEditRequest,
                                          @NotNull HttpServletRequest request) {
        ChartDetail oldChart = chartDetailService.getById(chartEditRequest.getId());
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        ChartDetail newChart = new ChartDetail();
        BeanUtils.copyProperties(chartEditRequest, newChart);
        return chartDetailService.updateById(newChart) ? ResultUtils.success(newChart.getId()) :
                ResultUtils.error(ErrorCode.UPDATE_ERROR, "Failed to edit chart: " + oldChart.getId());
    }

    @PostMapping("/generate/chart")
    public BaseResponse<MQTaskResponse> generateChart(@RequestPart("file") MultipartFile multipartFile,
                                                      @NotNull ChartAddRequest chartAddRequest,
                                                      @NotNull HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // todo 检查上传文件
        return chartDetailService.startChartGeneration(multipartFile, chartAddRequest, loginUser.getId());
    }


}
