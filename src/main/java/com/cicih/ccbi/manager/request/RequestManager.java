package com.cicih.ccbi.manager.request;

import com.cicih.ccbi.config.JsonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/callback")
public class RequestManager {
    private enum RequestType{
        GET, POST
    }

    private static final Map<String, RequestCallback> callbackCache = new ConcurrentHashMap<>();

    /**
     * Get 请求，同步处理，可以自定义处理逻辑
     */
    @NotNull
    public static <R> R get(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @NotNull ResponseHandler<R> responseHandler
    ) throws IOException {
        return Objects.requireNonNull(sendRequest(RequestType.GET,
            url,
            header,
            null,
            null,
            null,
            null,
            responseHandler
        ));
    }

    /**
     * Get 请求，同步处理，可以直接使用默认的处理器加上指定 TypeReference 来进行结果处理
     */
    @NotNull
    public static <R> R get(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @NotNull TypeReference<R> typeReference
    ) throws IOException {
        return Objects.requireNonNull(sendRequest(RequestType.GET,
            url,
            header,
            null,
            null,
            null,
            null,
            new DefaultResponseHandler<>(typeReference)
        ));
    }

    /**
     * Get 请求，异步处理，可以指定请求回调
     */
    public static void get(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @Nullable RequestCallback callback
    ) throws IOException {
        sendRequest(RequestType.GET, url, header, null, null, null, callback, new DefaultResponseHandler<>());
    }

    /**
     * Post 请求，同步处理，自定义拼装请求体
     */
    @NotNull
    public static <R> R post(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @NotNull ObjectNode requestBody,
        @NotNull TypeReference<R> typeReference
    ) throws IOException {
        return Objects.requireNonNull(sendRequest(RequestType.POST,
            url,
            header,
            requestBody,
            null,
            null,
            null,
            new DefaultResponseHandler<>(typeReference)
        ));
    }
    public static void post(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @Nullable ObjectNode body,
        @NotNull RequestCallback callback
    ) throws IOException {
        sendRequest(RequestType.POST,
            url,
            header,
            body,
            null,
            null,
            callback,
            new DefaultResponseHandler<>()
        );
    }

    /**
     * Post 请求，同步处理，可以指定请求回调
     */
    @NotNull
    public static <T, R> R post(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @Nullable T body,
        @Nullable Map<String, Object> extraBody,
        @Nullable CustomSerializer<T> customSerializer,
        @NotNull TypeReference<R> typeReference
    ) throws IOException {
        return Objects.requireNonNull(sendRequest(RequestType.POST,
            url,
            header,
            body,
            extraBody,
            customSerializer,
            null,
            new DefaultResponseHandler<>(typeReference)
        ));
    }

    public static <T> void post(
        @NotNull String url,
        @Nullable Map<String, String> header,
        @Nullable T body,
        @Nullable Map<String, Object> extraBody,
        @Nullable CustomSerializer<T> customSerializer,
        @NotNull RequestCallback callback
    ) throws IOException {
        sendRequest(RequestType.POST,
            url,
            header,
            body,
            extraBody,
            customSerializer,
            callback,
            new DefaultResponseHandler<>()
        );
    }

    /**
     * 发送请求
     *
     * @param requestType      请求类型，目前支持 GET 和 POST
     * @param url              请求地址
     * @param header           请求头，会覆盖默认请求头中的同名字段
     * @param body             请求体
     * @param extraBody        额外的请求体，会覆盖 body 中的同名字段
     * @param customSerializer 自定义序列化器，用于将 body 和 extraBody 序列化为请求体
     * @param callback         请求回调，如果为 null，则同步发送请求，否则异步发送请求并立即返回 null
     * @param responseHandler  响应处理器，用于在同步发送请求时处理响应，callback 和 responseHandler 不同时存在
     * @param <T>              请求体类型
     * @return 如果 callback 为 null，则返回响应处理器处理后的结果，否则返回 null
     * @throws IOException 如果发送请求或处理响应时发生错误，则抛出该异常
     */
    @Nullable
    private static <T, R> R sendRequest(
        @NotNull RequestType requestType,
        @NotNull String url,
        @Nullable Map<String, String> header,
        @Nullable T body,
        @Nullable Map<String, Object> extraBody,
        @Nullable CustomSerializer<T> customSerializer,
        @Nullable RequestCallback callback,
        @NotNull ResponseHandler<R> responseHandler
    ) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpRequestBase httpRequest;
            switch (requestType) {
                case GET:
                    httpRequest = new HttpGet(url);
                    break;
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    if (extraBody != null && customSerializer != null) {
                        log.warn("extraBody and customSerializer cannot be used at the same time, ignoring extraBody");
                    }
                    String requestBody;
                    if (customSerializer != null) {
                        requestBody = customSerializer.serialize(body, JsonConfig.snakeCaseObjectMapper);
                    } else if (extraBody != null && !extraBody.isEmpty()) {
                        ObjectNode objectNode = JsonConfig.snakeCaseObjectMapper.valueToTree(body);
                        for (Map.Entry<String, Object> entry : extraBody.entrySet()) {
                            objectNode.put(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                        requestBody = JsonConfig.snakeCaseObjectMapper.writeValueAsString(objectNode);
                    } else {
                        requestBody = JsonConfig.snakeCaseObjectMapper.writeValueAsString(body);
                    }

                    log.info("Send request to url {} with body {}", url, requestBody);
                    AbstractHttpEntity entity;
                    if (header != null && header.containsValue(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                        entity = new UrlEncodedFormEntity(convertToListOfNameValuePair(requestBody));
                    } else {
                        entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    }
                    entity.setContentEncoding("UTF-8");
                    httpPost.setEntity(entity);
                    httpRequest = httpPost;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported request type: " + requestType);
            }
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-Type", "application/json");
            httpRequest.setHeader("Accept-Charset", "utf-8");
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            }
            log.info("Send request to url {} with header {}", url, httpRequest.getAllHeaders());
            RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .setConnectTimeout(30000)
                .build();
            httpRequest.setConfig(requestConfig);
            if (callback == null) {
                CloseableHttpResponse response = client.execute(httpRequest);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException(
                        "Request failed with status code: " + response.getStatusLine().getStatusCode() + ", reason: " +
                        response.getStatusLine().getReasonPhrase() + ", body: " +
                        new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));
                }
                return responseHandler.handle(response);
            } else {
                String callbackId = String.valueOf(System.currentTimeMillis());
                httpRequest.setHeader("Success-Callback-Url", "http://generative-paw.mightypaw.info/callback/success");
                httpRequest.setHeader("Failure-Callback-Url", "http://generative-paw.mightypaw.info/callback/failure");
                httpRequest.setHeader("Callback-Id", callbackId);

                CloseableHttpResponse response = client.execute(httpRequest);
                if (response.getStatusLine().getStatusCode() != 200) {
                    callback.onFailure(response.getStatusLine().getStatusCode(),
                        response.getStatusLine().getReasonPhrase()
                    );
                } else {
                    callbackCache.put(callbackId, callback);
                }
                return null;
            }
        }
    }

    @PostMapping("/success")
    public void successCallbackHandler(@NotNull HttpServletRequest request) throws IOException {
        String callbackId = request.getHeader("Callback-Id");
        log.info("Get success callback with id: {}", callbackId);
        RequestCallback callback = callbackCache.remove(callbackId);
        if (callback != null) {
            StringBuilder requestBody = new StringBuilder();
            InputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            log.info("Success callback {} with body: {}", callbackId, requestBody);
            callback.onSuccess(requestBody.toString());
        }
    }

    @PostMapping("/failure")
    public void failCallbackHandler(@NotNull HttpServletRequest request) throws IOException {
        String callbackId = request.getHeader("Callback-Id");
        log.info("Get failure callback with id: {}", callbackId);
        RequestCallback callback = callbackCache.remove(callbackId);
        if (callback != null) {
            StringBuilder requestBody = new StringBuilder();
            InputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            log.info("Failure callback {} with body: {}", callbackId, requestBody);
            String body = requestBody.toString();
            if (body.startsWith("\"")) {
                body = body.substring(1);
            }
            if (body.endsWith("\"")) {
                body = body.substring(0, body.length() - 1);
            }
            callback.onFailure(200, body);
        }
    }

    private static List<NameValuePair> convertToListOfNameValuePair(@NotNull String serializedBody) throws JsonProcessingException {
        Map<String, String> map = JsonConfig.snakeCaseObjectMapper.readValue(serializedBody, new TypeReference<>() {});
        return map.entrySet().stream().map(entry -> new NameValuePair() {
            @Override
            public String getName() {
                return entry.getKey();
            }

            @Override
            public String getValue() {
                return entry.getValue();
            }
        }).collect(Collectors.toList());
    }

}
