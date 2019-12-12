package com.security;

import java.util.Locale;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Profile({ "default", "dev", "prod" })
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
		
		log.info("Generating datasource ...");
		
		dataSource.setUrl(configuration.getUrl()); //BLOG_MYSQL_URL
		dataSource.setUsername(configuration.getUsername()); //BLOG_MYSQL_USERNAME
		dataSource.setPassword(configuration.getPassword()); //BLOG_MYSQL_PASSWORD
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		return dataSource;
	}

	/**
	 * Creates a LocaleResolver with default locale Us.
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	@Bean
	@Profile("dev")
	public FlywayMigrationStrategy cleanMigrateStrategy() {
	    FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
		@Override
		public void migrate(Flyway flyway) {
		    flyway.clean();
		    flyway.migrate();
		}
	    };
	    return strategy;
	}
	
	/**
	 * Creates a message source resources.
	 * 
	 */
	@Bean
	public ResourceBundleMessageSource bundleMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}
	
}
