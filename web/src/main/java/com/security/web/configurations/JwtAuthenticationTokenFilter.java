package com.security.web.configurations;

import com.security.web.domain.User;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.services.UserService;
import com.security.web.domain.ApplicationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final UserDetailsService userDetailService;
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserService userService;

	public JwtAuthenticationTokenFilter(UserDetailsService userDetailService,
                                        JwtTokenGenerator jwtTokenUtil, UserService userService) {
		this.userDetailService = userDetailService;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, UnauthorizedUserException, ForbiddenUserException {

		String token = request.getHeader(AUTH_HEADER);
		if (token != null) {

			try {
				if (!token.startsWith(BEARER_PREFIX))
					throw new UnauthorizedUserException("Invalid token. Missing Bearer field.");

				token = token.substring(7);

				if (jwtTokenUtil.tokenIsValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {

					String userName = jwtTokenUtil.getUserNameFromToken(token);
					String applicationName = jwtTokenUtil.getApplicationName(token);
					if (userName == null || applicationName == null)
						throw new UnauthorizedUserException("Invalid token information. " + "Username: " + userName
								+ " ApplicationName: " + applicationName);

					log.info("Verifying token with user name {} and application name {}.", userName, applicationName);
					UserDetails userDetails = this.userDetailService.loadUserByUsername(userName);

					if (userDetails == null || !this.userService.existUserWithUserName(userName)) {
						log.info("Invalid token information.");
						throw new UnauthorizedUserException("Invalid token information.");
					}

					try {
						// Verify authorization
						User user = this.userService.findByUserName(userName);
						if (!user.hasRegistry(ApplicationType.valueOf(applicationName))) {
							String message = String.format(
									"Forbidden. The user %s doesn't have authorization " + "to access %s",
									user.getUserName(), applicationName);
							log.info(message);
							throw new ForbiddenUserException(message);
						}
						// Authentication process
						UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
								null, userDetails.getAuthorities());
						auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(auth);
					} catch (Exception e) {
						log.info("Error. {}", e.getMessage());
						throw e;
					}

				}
			} catch (Exception e) {
				throw new UnauthorizedUserException("Invalid token. Missing Bearer field.");
			}

		}

		filterChain.doFilter(request, response);
	}

}
