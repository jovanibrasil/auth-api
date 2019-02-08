package com.jwt.security.controllers;

import java.util.Optional;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONObject;
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
import com.jwt.security.dto.UserDto;
import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.repositories.UserRepository;
import com.jwt.security.utils.JwtTokenUtil;
import com.jwt.utils.PasswordUtils;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*")
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
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	 * Create and returns a new token JWT.
	 */
	@PostMapping("/login")
	public ResponseEntity<Response<TokenDto>> createTokenJwt(@Valid @RequestBody JwtAuthenticationDto authenticationDto, 
			BindingResult result) throws AuthenticationException {
		
		Response<TokenDto> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		log.info("Generating token {}", authenticationDto.getUserName());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDto.getUserName(), authenticationDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
				
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getUserName());
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
	
	/*
	 * Return a list of errors if the token has problems. Otherwise, returns an empty
	 * list of errors.
	 */
	@PostMapping(value="/checkToken")
	public ResponseEntity<Response<String>> checkToken(HttpServletRequest request){
		log.info("Cheking JWT token");
		Response<String> response = new Response<>();
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		
		if(token.isPresent() && token.get().startsWith(BEARER_PREFIX)) {
			token = Optional.of(token.get().substring(7));
		}

		if(!token.isPresent()) {
			log.error("The request do not contain a token");
			response.getErrors().add("The request do not contain a token.");
		}
		else if(!jwtTokenUtil.tokenIsValid(token.get())) {
			log.error("The token in invalid or expired");
			response.getErrors().add("The token is invalid or expired.");
		}
			
		if(!response.getErrors().isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}
			
		JSONObject json = new JSONObject();
		json.put("userName", jwtTokenUtil.getUserNameFromToken(token.get()));
		response.setData(json.toString());
	
		return ResponseEntity.ok(response);

	}
	
	@PostMapping(value="/signup")
	public ResponseEntity<Response<String>> sigupUser(@Valid @RequestBody UserDto userDto, BindingResult result){
		
		log.info("Creating new user");
		Response<String> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
	
		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setUserName(userDto.getUserName());
		user.setSignUpDate(userDto.getSignDate());
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setPassword(PasswordUtils.generateHash(userDto.getPassword()));
		this.userRepository.save(user);
		
		return ResponseEntity.ok(response);
	}
	
	
	
}
