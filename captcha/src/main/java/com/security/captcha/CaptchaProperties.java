package com.security.captcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Getter
@Setter
@ConfigurationProperties("captcha")
public class CaptchaProperties {

    private String recaptchakeysite;
    private String recaptchakeysecret;

}
