package com.cicih.ccbi.manager.request;

import com.cicih.ccbi.config.JsonConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@AllArgsConstructor
public class DefaultResponseHandler<T> implements ResponseHandler<T> {
    @NotNull
    private final TypeReference<T> typeReference;
    @NotNull
    private final ObjectMapper objectMapper;

    public DefaultResponseHandler() {
        this.typeReference = new TypeReference<>() {};
        objectMapper = JsonConfig.nullableObjectMapper;
    }

    public DefaultResponseHandler(@NotNull TypeReference<T> typeReference) {
        this.typeReference = typeReference;
        objectMapper = JsonConfig.nullableObjectMapper;
    }

    @NotNull
    @Override
    public T handle(@NotNull CloseableHttpResponse response) throws IOException {
        try (InputStream inputStream = response.getEntity().getContent()) {
            T result = objectMapper.readValue(inputStream, typeReference);
            log.info("Receive response {}.", result.toString());
            return result;
        }
    }
}

