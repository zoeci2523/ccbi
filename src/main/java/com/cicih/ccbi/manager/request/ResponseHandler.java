package com.cicih.ccbi.manager.request;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ResponseHandler<T> {
    T handle(@NotNull CloseableHttpResponse response) throws IOException;
}
