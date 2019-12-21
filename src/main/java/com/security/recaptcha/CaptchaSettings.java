package com.security.recaptcha;

import com.security.AuthDataSourceProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@EnableConfigurationProperties(AuthDataSourceProperties.class)
public class CaptchaSettings {

	private String site;
    private String secret;
    
    public CaptchaSettings(AuthDataSourceProperties configuration) {
    	this.site = configuration.getRecaptchakeysite();
    	this.secret = configuration.getRecaptchakeysecret();
    }

}
