package com.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@ConfigurationProperties("auth-cred")
public class AuthDataSourceProperties {

	private String username;
	private String url;
	private String password;
	private String recaptchakeysite;
	private String recaptchakeysecret;
	private String jwtsecretkey;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getRecaptchakeysite() {
		return recaptchakeysite;
	}

	public void setRecaptchakeysite(String recaptchakeysite) {
		this.recaptchakeysite = recaptchakeysite;
	}

	public String getRecaptchakeysecret() {
		return recaptchakeysecret;
	}

	public void setRecaptchakeysecret(String recaptchakeysecret) {
		this.recaptchakeysecret = recaptchakeysecret;
	}

	public String getJwtsecretkey() {
		return jwtsecretkey;
	}

	public void setJwtsecretkey(String jwtsecretkey) {
		this.jwtsecretkey = jwtsecretkey;
	}	
}
