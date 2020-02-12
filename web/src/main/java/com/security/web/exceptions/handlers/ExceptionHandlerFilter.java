package com.security.web.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.web.dto.Response;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			filterChain.doFilter(request, response);
			return;
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
		Response<String> res = new Response<String>();
		ObjectMapper mapper = new ObjectMapper();
		PrintWriter out = response.getWriter();
		out.print(mapper.writeValueAsString(res));
	}

}