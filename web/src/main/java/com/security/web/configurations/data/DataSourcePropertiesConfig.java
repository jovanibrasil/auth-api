package com.security.web.configurations.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile({ "dev", "prod", "stage" })
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourcePropertiesConfig {

	private final DataSourceProperties configuration;

	@Bean
	public DataSource getDataResource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		log.info("Generating datasource ...");
		
		dataSource.setUrl(configuration.getUrl());
		dataSource.setUsername(configuration.getUsername());
		dataSource.setPassword(configuration.getPassword());
		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
		
		return dataSource;
	}

}
