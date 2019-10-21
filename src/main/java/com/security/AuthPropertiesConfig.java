package com.security;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Profile("prod")
@Configuration
@EnableConfigurationProperties(AuthDataSourceProperties.class)
public class AuthPropertiesConfig {

	private static final Logger log = LoggerFactory.getLogger(AuthPropertiesConfig.class);
	
	private final AuthDataSourceProperties configuration;

	public AuthPropertiesConfig(AuthDataSourceProperties configuration) {
		this.configuration = configuration;
		log.info("Setting jwt secret ...");
		System.setProperty("jwt.secret", configuration.getJwtsecretkey());
	}

	@Bean
	public DataSource getDataResource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		log.info("Generatig datasource ...");
		
		dataSource.setUrl(configuration.getUrl()); //BLOG_MYSQL_URL
		dataSource.setUsername(configuration.getUsername()); //BLOG_MYSQL_USERNAME
		dataSource.setPassword(configuration.getPassword()); //BLOG_MYSQL_PASSWORD
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		return dataSource;
	}

}
