package com.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class AuthApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		log.info("Starting application ...");
		SpringApplication.run(AuthApplication.class, args);
	}

	@Bean
	public LocalValidatorFactoryBean validator(MessageSource messageSource) {
		final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource);
		return validator;
	}
	
}