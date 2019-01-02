package com.jwt.security.controllers;

import java.util.Optional;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.response.Response;
import com.jwt.security.dto.JwtAuthenticationDto;
import com.jwt.security.dto.TokenDto;
import com.jwt.security.utils.JwtTokenUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
	private static final String TOKEN_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/*
	 * Create and returns a new token JWT.
	 */
	@PostMapping
	public ResponseEntity<Response<TokenDto>> createTokenJwt(@Valid @RequestBody JwtAuthenticationDto authenticationDto, 
			BindingResult result) throws AuthenticationException {
		
		Response<TokenDto> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		log.info("Generating token {}", authenticationDto.getEmail());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
				
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getEmail());
		String token = jwtTokenUtil.createToken(userDetails);
		response.setData(new TokenDto(token));
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value="/refresh")
	public ResponseEntity<Response<TokenDto>> refreshTokenJwt(HttpServletRequest request){
		
		log.info("Refreshing JWT token");
		
		Response<TokenDto> response = new Response<>();
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		
		if(token.isPresent() && token.get().startsWith(BEARER_PREFIX))
			token = Optional.of(token.get().substring(7));

		if(!token.isPresent())
			response.getErrors().add("The request do not contain a token.");
		else if(!jwtTokenUtil.tokenIsValid(token.get()))
			response.getErrors().add("The token is invalid or expired.");
		
		if(!response.getErrors().isEmpty())
			return ResponseEntity.badRequest().body(response);
			
		String refreshedToken = jwtTokenUtil.refreshToken(token.get());
		response.setData(new TokenDto(refreshedToken));
		
		return ResponseEntity.ok(response);
	}
	
	
	
}
