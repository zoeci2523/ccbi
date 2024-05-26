package com.cicih.ccbi.controller;

import com.cicih.ccbi.common.BaseResponse;
import com.cicih.ccbi.common.ResultUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiSender {

    private static final String CHAT_ENDPOINT = "";

    // TODO 完善发送请求服务，但也有可能使用sdk
    public static BaseResponse<String> chatService(@NotNull String prompt){
        String result = null;
        return ResultUtils.success(result);
    }
}
