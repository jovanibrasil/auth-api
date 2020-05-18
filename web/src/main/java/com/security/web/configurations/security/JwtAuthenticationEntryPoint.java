package com.security.web.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.web.configurations.data.CustomMessageSource;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private CustomMessageSource msgSrc;

	public JwtAuthenticationEntryPoint(CustomMessageSource msgSrc) {
		this.msgSrc = msgSrc;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getOutputStream().println(new ObjectMapper()
				.writeValueAsString(msgSrc.getMessage("error.unauthorized", LocaleContextHolder.getLocale())));
		
	}	
	
}
