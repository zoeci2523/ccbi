package com.cicih.ccbi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * Swagger API 文档的配置
 * 集成到 SpringBoot 中时，要放在 SpringBoot 启动类 同级的目录下
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev", "test"})
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .enable(true) // 默认开启
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.cicih.ccbi.controller"))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("CCBI API Doc")
            .description("com.cicih.ccbi")
            .version("1.0")
            .build();
    }

}
