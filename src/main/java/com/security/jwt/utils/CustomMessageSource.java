package com.security.jwt.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CustomMessageSource {

    private MessageSource messageSource;

    public CustomMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Returns language specific from the resources.
     *
     * @param name is the field name
     * @param locale is the locale detected by springboot.
     * @return
     */
    public String getMessage(String name, Locale locale) {
        return messageSource.getMessage(name, null, locale);
    }

}
