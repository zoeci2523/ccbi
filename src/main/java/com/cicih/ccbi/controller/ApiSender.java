package com.cicih.ccbi.controller;

import com.cicih.ccbi.manager.request.RequestManager;
import com.cicih.ccbi.model.dto.api.ChatRequest;
import com.cicih.ccbi.model.dto.api.ChatResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ApiSender {

    private static final String CHAT_API_KEY = "409d1965f9856db4e1702139dc618b9cf9d93ceb2700f4a65b8834bede73ccac";
    private static final String CHAT_SERVICE_API_ENDPOINT = "https://api.together.xyz/v1/chat/completions";

    public static ChatResponse describeChartSync(@NotNull ChatRequest request){
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

    // TODO 补充异步生成图功能
    public static ChatResponse describeChartAsync(@NotNull ChatRequest request){
        return null;
    }
}
