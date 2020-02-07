package com.security.web.configurations;

import com.security.web.exceptions.handlers.RestTemplateResponseErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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
