package com.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Getter @Setter
@ToString
@Profile({ "default", "prod", "stage" })
@ConfigurationProperties("auth-cred")
public class AuthDataSourceProperties {

	private String username;
	private String url;
	private String password;
	private String recaptchakeysite;
	private String recaptchakeysecret;
	private String jwtsecretkey;

}
