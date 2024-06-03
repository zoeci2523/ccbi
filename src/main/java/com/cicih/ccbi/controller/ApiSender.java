package com.cicih.ccbi.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cicih.ccbi.common.ErrorCode;
import com.cicih.ccbi.config.ThreadPoolExecutorConfig;
import com.cicih.ccbi.exception.BusinessException;
import com.cicih.ccbi.manager.request.RequestCallback;
import com.cicih.ccbi.manager.request.RequestManager;
import com.cicih.ccbi.model.dto.api.ChatRequest;
import com.cicih.ccbi.model.dto.api.ChatResponse;
import com.cicih.ccbi.model.entity.Task;
import com.cicih.ccbi.service.ChartDetailService;
import com.cicih.ccbi.service.TaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class ApiSender {

    private static final String CHAT_API_KEY = "409d1965f9856db4e1702139dc618b9cf9d93ceb2700f4a65b8834bede73ccac";
    private static final String CHAT_SERVICE_API_ENDPOINT = "https://api.together.xyz/v1/chat/completions";

    @Resource
    ChartDetailService chartService;
    @Resource
    TaskService taskService;

    public static ChatResponse describeChartSync(@NotNull ChatRequest request){
        // TODO 设置超时处理
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Bearer " + CHAT_API_KEY); // add authorization
            ObjectNode body = request.buildTogetherAiChatRequest();
            return RequestManager.post(CHAT_SERVICE_API_ENDPOINT, header, body, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Fail to describe chart with user input prompt: " + request.getPrompt(), e);
            return null;
        }
    }

    public ChatResponse describeChartAsync(@NotNull ChatRequest request){
        // demo
        String taskName = "test-task";
        CompletableFuture.runAsync(() -> {
            log.info("Running task: {}, executor: {}", taskName, Thread.currentThread().getName());
            try{
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + CHAT_API_KEY); // add authorization
                ObjectNode body = request.buildTogetherAiChatRequest();
                RequestManager.post(CHAT_SERVICE_API_ENDPOINT, header, body, new RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        String[] splits = result.split("<<<<<");
                        if (splits.length < 3){
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Error during AI service");
                        }
                        String genChart = splits[1].trim();
                        String genResult = splits[2].trim();
                        chartService.updateGenChartResult(chartService.getChartByTaskId(request.getTaskId()).getId(), genChart, genResult);
                        taskService.updateTaskStatus(request.getTaskId(), Task.Status.SUCCEED);
                    }

                    @Override
                    public void onFailure(
                        Integer statusCode,
                        String errorResponse
                    ) {
                        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", request.getTaskId());
                        updateWrapper.set("status", Task.Status.FAILED);
                        updateWrapper.set(errorResponse != null, "execMessage", errorResponse);
                        taskService.update(updateWrapper);
                    }
                });
            }catch (Exception e){
                log.error("Fail to describe chart with user input prompt: " + request.getPrompt(), e);
            }
        }, new ThreadPoolExecutorConfig().taskExecutor());
        return null;
    }
}
