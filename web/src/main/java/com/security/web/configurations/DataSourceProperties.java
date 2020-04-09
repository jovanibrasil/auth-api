package com.security.web.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties("db")
public class DataSourceProperties {

	private String username;
	private String url;
	private String password;

}
