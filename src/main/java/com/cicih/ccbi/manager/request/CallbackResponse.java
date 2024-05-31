package com.cicih.ccbi.manager.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class CallbackResponse {
    private Integer code;
    private String msg;

    @Getter
    @AllArgsConstructor
    public enum Msg{
        SUCCESS("Success"),
        FAILED("Failed"),
        TIMEOUT("Timeout");

        private String description;
    }
}
