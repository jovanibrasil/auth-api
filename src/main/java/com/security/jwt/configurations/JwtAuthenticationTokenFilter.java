package com.security.jwt.configurations;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.jwt.entities.User;
import com.security.jwt.exceptions.implementations.ForbiddenUserException;
import com.security.jwt.exceptions.implementations.UnauthorizedUserException;
import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.ApplicationType;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private UserDetailsService userDetailService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

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
					Optional<User> optUser = this.userService.findByUserName(userName);

					if (userDetails == null || !optUser.isPresent()) {
						log.info("Invalid token information.");
						throw new UnauthorizedUserException("Invalid token information.");
					}

					try {
						// Verify authorization
						User user = optUser.get();
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
