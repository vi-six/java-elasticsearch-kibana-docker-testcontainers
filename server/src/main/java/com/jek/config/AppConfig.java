package com.jek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@PropertySource("classpath:application.conf")
public class AppConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .groupName("1. Controllers")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jek.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public Docket actuator() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .groupName("2. Actuator")
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.springframework.boot.actuate"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Java-Elasticsearch-Kibana")
                .description("Demo java application that works with Elasticsearch through two different types of Clients. REST and Java API clients are shown here")
                .version("1.0")
                .build();
    }
}