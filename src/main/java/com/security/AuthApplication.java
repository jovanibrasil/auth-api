package com.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AuthApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(AuthApplication.class);
	
	public static void main(String[] args) {
		log.info("Starting application ...");
		SpringApplication.run(AuthApplication.class, args);
	}
	
}
