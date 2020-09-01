package com.security.web.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.jovanibrasil.captcha.EnableRecaptchaVerification;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.exception.handler.ExceptionHandlerFilter;
import com.security.web.service.UserService;

@Configuration
@EnableWebSecurity
@EnableRecaptchaVerification
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtAuthenticationEntryPoint unauthorizedHandler; // is an exception
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserService userService;

	public WebSecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, 
			JwtTokenGenerator jwtTokenUtil, @Lazy UserService userService) {
		this.unauthorizedHandler = unauthorizedHandler;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
	}

	/*
	 * Configures AuthenticationManager.
	 * 
	 * 
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}
	
	/*
	 * Filter used when the application intercepts a requests.
	 */
	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationTokenFilter(jwtTokenUtil, userService);
	}
	
	@Bean
	public ExceptionHandlerFilter exceptionHandlerFilterBean() throws Exception {
		return new ExceptionHandlerFilter();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.csrf().disable() // disable CSRF default protection 
			.cors()
			.and() // session isn't necessary with JWT based authentication
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // then disable session
			.and()
				.authorizeRequests()
					.antMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("SERVICE", "ADMIN", "USER")
					.antMatchers(HttpMethod.POST, "/users").permitAll()
					.antMatchers(HttpMethod.HEAD, "/users").permitAll()
					.antMatchers(HttpMethod.HEAD, "/users/*").permitAll()
					.antMatchers("/users/**").hasAnyRole("SERVICE", "ADMIN")
					.antMatchers("/token/*").permitAll()
					.antMatchers("/token/check").hasRole("SERVICE")
			.and()
				.addFilterBefore(authenticationTokenFilterBean(), BasicAuthenticationFilter.class)
				.addFilterBefore(exceptionHandlerFilterBean(), JwtAuthenticationTokenFilter.class)
				.exceptionHandling()
					.authenticationEntryPoint(unauthorizedHandler); // set authentication error
		httpSecurity.headers().cacheControl();
	}
	
	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	

}
