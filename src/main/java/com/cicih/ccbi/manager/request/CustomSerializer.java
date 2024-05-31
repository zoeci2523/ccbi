package com.cicih.ccbi.manager.request;


import com.fasterxml.jackson.databind.ObjectMapper;

public interface CustomSerializer<T> {

    String serialize(T object, ObjectMapper objectMapper);
}
