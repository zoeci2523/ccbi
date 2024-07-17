package com.cicih.ccbi.model.dto.api;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Prompt> prompt;
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Prompt {
        private String text;
        private Object logprobs; // 默认不开启，返回为 null
    }

    @Data
    public static class Choice {
        private String finish_reason;
        private BigInteger seed;
        private Object logprobs; // 默认不开启，返回为 null
        private int index;
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

}


