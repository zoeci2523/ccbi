package com.cicih.ccbi.model.dto.api;

import com.cicih.ccbi.config.JsonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@NoArgsConstructor
public class ChatRequest {
    private static final String TOGETHER_AI_CHAT_MODEL = "meta-llama/Llama-3-70b-chat-hf";
    private static final Integer DEFAULT_MAX_TOKEN = 50;

    @NotNull
    private String taskId;
    @NotNull
    private String prompt; // user prompt
    @Nullable
    private String systemPrompt;
    @Nullable
    private String model;
    @Nullable
    private Integer maxTokens;

    public ChatRequest(@NotNull String prompt){
        this.prompt = prompt;
    }

    public ChatRequest(@NotNull String taskId, @NotNull String systemPrompt, @NotNull String prompt){
        this.taskId = taskId;
        this.systemPrompt = systemPrompt;
        this.prompt = prompt;
    }

    @NotNull
    public ObjectNode buildTogetherAiChatRequest() {
        /**
         * https://docs.together.ai/docs/logprobs
         * template:
         *  {
         *      "model": "meta-llama/Llama-3-8b-chat-hf",
         *      "stream": false,
         *      "max_tokens": 10,
         *      "messages": [
         *        {"role": "system", "content": "{$system_prompt}"}
         *        {"role": "user", "content": "{$prompt}"}
         *      ],
         *      "echo": true // to get the result
         * }
         */
        ObjectMapper mapper = JsonConfig.snakeCaseObjectMapper;
        ObjectNode request = mapper.createObjectNode();
        request.put("model", TOGETHER_AI_CHAT_MODEL);
        request.put("stream", false);
        request.put("max_tokens", maxTokens != null ? maxTokens : DEFAULT_MAX_TOKEN);
        ArrayNode arrayNode = mapper.createArrayNode();
        ObjectNode messageNode = mapper.createObjectNode();
        if (systemPrompt != null) {
            messageNode.put("role", "system");
            messageNode.put("content", systemPrompt);
            arrayNode.add(messageNode);
        }
        messageNode.put("role", "user");
        messageNode.put("content", prompt);
        arrayNode.add(messageNode);
        request.putIfAbsent("message", arrayNode);
        request.put("echo", true);
        return request;
    }

}
