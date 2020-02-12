package com.security.web.configurations;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.exceptions.handlers.ExceptionHandlerFilter;
import com.security.web.services.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true) // evaluate using methods 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtAuthenticationEntryPoint unauthorizedHandler; // is an exception
	private final UserDetailsService userDetailsService;
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserService userService;

	public WebSecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                             @Lazy @Qualifier("userDetailServiceImpl") UserDetailsService userDetailsService,
                             JwtTokenGenerator jwtTokenUtil, @Lazy UserService userService) {
		this.unauthorizedHandler = unauthorizedHandler;
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
	}

	/*
	 * Configures AuthenticationManager.
	 * 
	 * 
	 */
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
		authenticationManagerBuilder.authenticationProvider(authenticationProvider());
	}
	
	/**
	 * Configures the authentication provider
	 * 
	 * @return
	 */
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
	
	/*
	 * Return an encoder, in this case a BCryptPasswordEncoder.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	 * Filter used when the application intercepts a requests.
	 */
	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationTokenFilter(userDetailsService, jwtTokenUtil, userService);
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
					.antMatchers("/token/**", "/users/**")
					.permitAll()
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
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
