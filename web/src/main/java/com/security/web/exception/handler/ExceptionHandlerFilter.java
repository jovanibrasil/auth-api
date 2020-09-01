package com.security.web.exception.handler;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.security.web.exception.implementation.ForbiddenUserException;
import com.security.web.exception.implementation.UnauthorizedUserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			filterChain.doFilter(request, response);
		} catch (UnauthorizedUserException e) {
			log.info("UnauthorizedUserException was throwed. {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (ForbiddenUserException e) {
			log.info("ForbiddenUserException was throwed. {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
			log.info("Exception. {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
