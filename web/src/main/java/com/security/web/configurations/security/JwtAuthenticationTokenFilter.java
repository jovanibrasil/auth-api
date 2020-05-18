package com.security.web.configurations.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.domain.User;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import com.security.web.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserService userService;
	
	public JwtAuthenticationTokenFilter(JwtTokenGenerator jwtTokenUtil, UserService userService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = request.getHeader(AUTH_HEADER);
		if(token != null) {
			try {
				if (!token.startsWith(BEARER_PREFIX))
					throw new UnauthorizedUserException("Invalid token. Missing Bearer field.");
				token = token.substring(7);
				
				if(jwtTokenUtil.tokenIsValid(token)) {
					User user = userService.findUserByUserName(jwtTokenUtil.getUserNameFromToken(token));
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,
							null, user.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(auth);
				}	
			} catch (Exception e) {
				log.error("User not found. Error: {}", e.getMessage());
				response.sendError(401);
			}
		}

		filterChain.doFilter(request, response);
	}

}
