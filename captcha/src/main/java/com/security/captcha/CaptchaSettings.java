package com.security.captcha;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter @Setter
@Component
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaSettings {

	private String site;
    private String secret;

    public CaptchaSettings(CaptchaProperties configuration) {
    	this.site = configuration.getRecaptchakeysite();
    	this.secret = configuration.getRecaptchakeysecret();
    }

}
