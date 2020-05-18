package com.security.web.configurations.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile({ "dev", "prod", "stage" })
@Getter @Setter
@ConfigurationProperties("db")
public class DataSourceProperties {

	private String username;
	private String url;
	private String password;

}
