package com.security.web.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.security.web.exception.handler.RestTemplateResponseErrorHandler;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate rt = restTemplateBuilder.build();
        rt.setErrorHandler(new RestTemplateResponseErrorHandler());
        return rt;
    }

}
