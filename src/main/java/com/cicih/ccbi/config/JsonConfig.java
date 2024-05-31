package com.cicih.ccbi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonConfig {
    /**
     * disable() 在序列化/反序列化过程中禁用某些特征
     *
     * 常用的 DeserializationFeature:
     * FAIL_ON_UNKNOWN_PROPERTIES：如果 JSON 中存在未知属性，则抛出异常。
     * FAIL_ON_NULL_FOR_PRIMITIVES：如果 JSON 中的原始类型字段为 null，则抛出异常。
     * FAIL_ON_NUMBERS_FOR_ENUMS：如果 JSON 中的枚举字段是一个数值，则抛出异常。
     * WRAP_EXCEPTIONS：抛出包装异常。
     */

    public static final ObjectMapper commonObjectMapper = new ObjectMapper();
    public static final ObjectMapper nullableObjectMapper = nullableObjectMapper();
    public static final ObjectMapper notNullObjectMapper = notNullObjectMapper();
    public static final ObjectMapper snakeCaseObjectMapper = snakeCaseObjectMapper();


    @Bean
    public static ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().createXmlMapper(false).build();
        SimpleModule module = new SimpleModule();
        // 添加自定义序列化工具，解决 Long 转 json 精度丢失的问题
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static ObjectMapper nullableObjectMapper(){
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .build();
        // 出现未知属性or忽略属性时忽略它，不抛出异常
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    public static ObjectMapper notNullObjectMapper(){
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    public static ObjectMapper snakeCaseObjectMapper(){
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

}