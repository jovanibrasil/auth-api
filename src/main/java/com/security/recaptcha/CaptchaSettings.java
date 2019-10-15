package com.security.recaptcha;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.security.AuthDataSourceProperties;

@Component
@EnableConfigurationProperties(AuthDataSourceProperties.class)
public class CaptchaSettings {

	private String site;
    private String secret;
    
    public CaptchaSettings(AuthDataSourceProperties configuration) {
    	this.site = configuration.getRecaptchakeysite();
    	this.secret = configuration.getRecaptchakeysecret();
    }
	
    public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
}
