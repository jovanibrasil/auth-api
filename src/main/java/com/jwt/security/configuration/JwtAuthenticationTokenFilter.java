package com.jwt.security.configuration;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwt.security.entities.User;
import com.jwt.security.services.impl.UserServiceImpl;
import com.jwt.security.utils.JwtTokenUtil;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserServiceImpl userService;
	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		
//		String token = request.getHeader(AUTH_HEADER);
//		if(token != null && token.startsWith(BEARER_PREFIX))
//			token = token.substring(7);
//		
//		String userName = jwtTokenUtil.getUserNameFromToken(token);
//		String applicationName = jwtTokenUtil.getApplicationName(token);
//		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//			UserDetails userDetails = this.userDetailService.loadUserByUsername(userName);
//			Optional<User> optUser = this.userService.getUserByName(userName);
//			
//			if(userDetails != null && jwtTokenUtil.tokenIsValid(token) && optUser.isPresent()) {
//				User user = optUser.get();
//				if(user.getMyApplications().contains(applicationName)) {
//					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//							userDetails, null, userDetails.getAuthorities());
//					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//					SecurityContextHolder.getContext().setAuthentication(auth);
//				}
//			}
//		}
//		
//		filterChain.doFilter(request, response);
//	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = request.getHeader(AUTH_HEADER);
		if(token != null && token.startsWith(BEARER_PREFIX))
			token = token.substring(7);
		
		String userName = jwtTokenUtil.getUserNameFromToken(token);
		String applicationName = jwtTokenUtil.getApplicationName(token);
		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailService.loadUserByUsername(userName);
			Optional<User> optUser = this.userService.getUserByName(userName);
			
			if(userDetails != null && jwtTokenUtil.tokenIsValid(token) && optUser.isPresent()) {
				User user = optUser.get();
				if(user.getMyApplications().contains(applicationName)) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
}
