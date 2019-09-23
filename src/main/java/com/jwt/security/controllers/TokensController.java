package com.jwt.security.controllers;

import java.util.Optional;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.response.Response;
import com.jwt.security.dto.JwtAuthenticationDto;
import com.jwt.security.dto.TokenDto;
import com.jwt.security.entities.TempUser;
import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.services.UserService;
import com.jwt.security.utils.JwtTokenUtil;
import com.jwt.utils.ApplicationType;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/token")
public class TokensController {

	private static final Logger log = LoggerFactory.getLogger(TokensController.class);
	
	private static final String TOKEN_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
		
	/**
	 * Create and returns a new token JWT.
	 */
	@PostMapping("/create")
	public ResponseEntity<Response<TokenDto>> createTokenJwt(@Valid @RequestBody JwtAuthenticationDto authenticationDto) throws AuthenticationException {
		log.info("Creating JWT token ...");
		Response<TokenDto> response = new Response<>();
		
		// Verify if user has register for the required application
		Optional<User> optUser = userService.findByUserName(authenticationDto.getUserName());
		if(optUser.isPresent()) {
			if(!optUser.get().hasRegistry(authenticationDto.getApplication())) {
				log.error("Authentication error. User not register for {}", authenticationDto.getApplication());
				response.addError("Authentication error. User not registered for this application.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);		
			}
		}
		
		// Does user authentication 
		log.info("Generating token {}", authenticationDto.getUserName());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDto.getUserName(),
						authenticationDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		// Verify authentication result
		if(!auth.isAuthenticated()) {
			log.error("Authentication error {}");
			response.addError("Authentication error. Invalid user name or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);		
		}
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getUserName());
		String token = jwtTokenUtil.createToken(userDetails, authenticationDto.getApplication());
		response.setData(new TokenDto(token));
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value="/refresh")
	public ResponseEntity<Response<TokenDto>> refreshTokenJwt(HttpServletRequest request){
		
		log.info("Refreshing JWT token");
		
		Response<TokenDto> response = new Response<>();
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		
		if(token.isPresent() && token.get().startsWith(BEARER_PREFIX))
			token = Optional.of(token.get().substring(7));

		if(!token.isPresent())
			response.addError("The request do not contain a token.");
		else if(!jwtTokenUtil.tokenIsValid(token.get()))
			response.addError("The token is invalid or expired.");
		
		if(!response.getErrors().isEmpty())
			return ResponseEntity.badRequest().body(response);
			
		String refreshedToken = jwtTokenUtil.refreshToken(token.get());
		response.setData(new TokenDto(refreshedToken));
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Return a list of errors if the token has problems. Otherwise, returns an empty
	 * list of errors.
	 */
	@GetMapping(value="/check")
	public ResponseEntity<Response<TempUser>> checkToken(HttpServletRequest request){
		log.info("Cheking JWT token");
		Response<TempUser> response = new Response<>();
		
		Optional<String> optToken = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		if(optToken.isPresent()) {
			if(optToken.get().startsWith(BEARER_PREFIX)) {
				optToken = Optional.of(optToken.get().substring(7));
			}else {
				log.error("Invalid token");
				response.addError("Invalid token.");
			}
		}
	
		if(!optToken.isPresent()) {
			log.error("The request do not contain a token");
			response.addError("The request do not contain a token.");
		}else {
			String token = optToken.get();
			if(!jwtTokenUtil.tokenIsValid(token)) {
				log.error("The token in invalid or expired");
				response.addError("The token is invalid or expired.");
			}else {
				log.info("The token is valid");
			
				// Verify if user has register for the required application
				String userName = jwtTokenUtil.getUserNameFromToken(token);
				Optional<User> optUser = userService.findByUserName(userName);
				if(optUser.isPresent()) {
					ApplicationType applicationName = ApplicationType.valueOf(jwtTokenUtil.getApplicationName(token));
					if(!applicationName.equals(ApplicationType.AUTH_APP) && !optUser.get().hasRegistry(applicationName)) {
						log.error("Authentication error {} not registered for application {}.");
						response.addError("Authentication error. User not registered for this application.");
					}
				}else {
					log.error("User {} not found.", userName);
					response.addError("User not found.");
					return ResponseEntity.badRequest().body(response);
				}
			}
		}
			
		if(!response.getErrors().isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}
		String userName = jwtTokenUtil.getUserNameFromToken(optToken.get());
		log.info("Token ok from user {}", userName);
		TempUser tempUser = new TempUser();
		tempUser.setName(userName);
		tempUser.setRole(ProfileEnum.valueOf(jwtTokenUtil.getAuthority(optToken.get())));
		
		response.setData(tempUser);
		return ResponseEntity.ok(response);

	}
	
}
