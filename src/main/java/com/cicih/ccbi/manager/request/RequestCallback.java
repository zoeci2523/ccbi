package com.cicih.ccbi.manager.request;

public interface RequestCallback {
    void onSuccess(String jsonResponse);
    void onFailure(Integer statusCode, String errorResponse);
}
