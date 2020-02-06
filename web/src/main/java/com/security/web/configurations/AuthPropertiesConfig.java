package com.security.web.configurations;

import com.security.captcha.AuthDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Profile({ "default", "prod", "stage" })
@Configuration
@EnableConfigurationProperties(AuthDataSourceProperties.class)
public class AuthPropertiesConfig {

	private final AuthDataSourceProperties configuration;

	public AuthPropertiesConfig(AuthDataSourceProperties configuration) {
		this.configuration = configuration;
		log.info("Setting jwt secret ...");
		System.setProperty("jwt.secret", configuration.getJwtsecretkey());
	}

	@Bean
	public DataSource getDataResource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		log.info("Generating datasource ...");
		
		dataSource.setUrl(configuration.getUrl());
		dataSource.setUsername(configuration.getUsername());
		dataSource.setPassword(configuration.getPassword());
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		return dataSource;
	}

}
