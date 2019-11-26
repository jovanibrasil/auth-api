package com.security.jwt.controllers;

import java.util.Locale;
import java.util.Optional;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.jwt.dto.JwtAuthenticationDTO;
import com.security.jwt.dto.TokenDTO;
import com.security.jwt.entities.TempUser;
import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.exceptions.implementations.ForbiddenUserException;
import com.security.jwt.exceptions.implementations.InvalidTokenException;
import com.security.jwt.exceptions.implementations.UnauthorizedUserException;
import com.security.jwt.response.Response;
import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.ApplicationType;
import com.security.recaptcha.CaptchaService;
import com.security.recaptcha.InvalidRecaptchaException;
import com.security.recaptcha.ReCaptchaInvalidException;

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
		
	@Autowired
	private CaptchaService captchaService;
	
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Creates and returns a new token JWT.
	 * 
	 * @param authenticationDto
	 * @param request
	 * @return
	 * @throws AuthenticationException
	 * @throws InvalidRecaptchaException
	 * @throws ReCaptchaInvalidException
	 */
	@PostMapping("/create")
	public ResponseEntity<Response<TokenDTO>> createTokenJwt(@Valid @RequestBody JwtAuthenticationDTO authenticationDto,
			HttpServletRequest request, @RequestHeader(name="Accept-Language", required = false) Locale locale) 
					throws AuthenticationException, InvalidRecaptchaException, ReCaptchaInvalidException {
		
		log.info("User {} is requesting a JWT token.", authenticationDto.getUserName());
		//String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		//captchaService.processResponse(recaptchaResponse);
		
		// Verify if user has registry for the required application
		Optional<User> optUser = userService.findByUserName(authenticationDto.getUserName());
		if(optUser.isPresent()) {
			if(!optUser.get().hasRegistry(authenticationDto.getApplication())) {
				log.error("Authentication error. User not register for {}", authenticationDto.getApplication());
				throw new ForbiddenUserException(messageSource.getMessage("user.not.register.for.application", null, locale));
			}
		}else {
			throw new UnauthorizedUserException(messageSource.getMessage("invalid.input", null, locale));
		}
		
		// Does user authentication 
		log.info("Authenticating {} ...", authenticationDto.getUserName());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDto.getUserName(), authenticationDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		// Verify authentication result
		if(!auth.isAuthenticated()) {
			log.error("Authentication error {}");
			throw new UnauthorizedUserException(messageSource.getMessage("invalid.input", null, locale));		
		}
		log.info("Creating token for {}", authenticationDto.getUserName());
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getUserName());
		String token = jwtTokenUtil.createToken(userDetails, authenticationDto.getApplication());
		log.info("Token successfully generated for {}.", authenticationDto.getUserName());
		Response<TokenDTO> response = new Response<>();
		response.setData(new TokenDTO(token));
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value="/refresh")
	public ResponseEntity<Response<TokenDTO>> refreshTokenJwt(HttpServletRequest request, 
			@RequestHeader(name="Accept-Language", required = false) Locale locale){
		
		log.info("Refreshing JWT token");
		
		Response<TokenDTO> response = new Response<>();
		try {
			Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
			
			if(token.isPresent()) {
				if(token.get().startsWith(BEARER_PREFIX)) {
					token = Optional.of(token.get().substring(7));
				}
				if(!jwtTokenUtil.tokenIsValid(token.get())) {
					response.addError(messageSource.getMessage("invalid.or.expired.token", null, locale));
				}
			}else {
				response.addError(messageSource.getMessage("request.without.token", null, locale));
			}
			
			if(!response.getErrors().isEmpty())
				return ResponseEntity.badRequest().body(response);
				
			String refreshedToken = jwtTokenUtil.refreshToken(token.get());
			response.setData(new TokenDTO(refreshedToken));
			
			return ResponseEntity.ok(response);
			
		} catch (InvalidTokenException e) {
			response.addError(messageSource.getMessage("invalid.token", null, locale) + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	/**
	 * Return a list of errors if the token has problems. Otherwise, returns an empty
	 * list of errors.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value="/check")
	public ResponseEntity<Response<TempUser>> checkToken(HttpServletRequest request, 
			@RequestHeader(name="Accept-Language", required = false) Locale locale){
		
		log.info("Checking JWT token");
		
		Response<TempUser> response = new Response<>();
		try {
			Optional<String> optToken = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
			if(optToken.isPresent()) {
				if(optToken.get().startsWith(BEARER_PREFIX)) {
					optToken = Optional.of(optToken.get().substring(7));
				}else {
					log.error("Invalid token");
					response.addError(messageSource.getMessage("invalid.token", null, locale));
				}
			}
		
			if(!optToken.isPresent()) {
				log.error("The request do not contain a token");
				response.addError(messageSource.getMessage("request.without.token", null, locale));
			}else {
				String token = optToken.get();
				if(!jwtTokenUtil.tokenIsValid(token)) {
					log.error("The token in invalid or expired");
					response.addError(messageSource.getMessage("invalid.or.expired.token", null, locale));
				}else {
					// Verify if user has register for the required application
					String userName = jwtTokenUtil.getUserNameFromToken(token);
					Optional<User> optUser = userService.findByUserName(userName);
					if(optUser.isPresent()) {
						ApplicationType applicationName = ApplicationType.valueOf(jwtTokenUtil.getApplicationName(token));
						if(!applicationName.equals(ApplicationType.AUTH_APP) && !optUser.get().hasRegistry(applicationName)) {
							log.error("Authentication error {} not registered for application {}.");
							response.addError(messageSource.getMessage("user.not.register.for.application", null, locale));
						}
					}else {
						log.error("User {} not found.", userName);
						response.addError(messageSource.getMessage("user.not.found", null, locale));
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
		} catch (InvalidTokenException e) {
			response.addError(messageSource.getMessage("invalid.token", null, locale) + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}

	}
	
}
