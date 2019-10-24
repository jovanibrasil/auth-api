package com.security.jwt.controllers;

import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.security.jwt.dto.DTOUtils;
import com.security.jwt.dto.TokenDto;
import com.security.jwt.dto.UserDto;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.exceptions.UserServiceException;
import com.security.jwt.integration.EmailMessage;
import com.security.jwt.integration.Integration;
import com.security.jwt.response.Response;
import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.services.UserService;
import com.security.recaptcha.CaptchaService;
import com.security.recaptcha.InvalidRecaptchaException;
import com.security.recaptcha.ReCaptchaInvalidException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {
		
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
		
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private Integration integration;
	
	@Autowired
	private CaptchaService captchaService;
	
	@Value("${urls.notes.userconfirmationview}")
	private String userConfirmationViewUrl;
	
	@GetMapping("/{userName}")
	public ResponseEntity<Response<UserDto>> getUser(@PathVariable String userName) {
		
		Response<UserDto> response = new Response<UserDto>();
		
		Optional<User> optUser = userService.findByUserName(userName);
		
		if(!optUser.isPresent()) {
			log.info("User {} not found!", userName);
			return ResponseEntity.badRequest().body(null);
		}
		User user = optUser.get();
		UserDto userDto = new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setUserName(user.getUserName());
		for (Registry r : user.getRegistries()) {
			log.info("Application: {}", r.getApplication().getApplication().name());
		}
		response.setData(userDto);
		
		return ResponseEntity.ok(response);
		
	}
	
	@PostMapping("/confirmation")
	public ResponseEntity<Response<?>> confirmUserCreation(@Valid @NotBlank @RequestParam String token){
		log.info("User confirmation");
		Response<?> response = new Response<>();
		try {
			// Get user informations from token
			User user = jwtTokenUtil.getUserFromToken(token);
			log.info("Saving user: [Name: {} Email: {}]", user.getUserName(), user.getEmail());
			user = this.userService.save(user);
			
			// Return the resource location
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(user.getId())
					.toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_LOCATION, location.toString());
			return ResponseEntity.status(HttpStatus.CREATED)
					.headers(headers).body(response);
			
		} catch (UserServiceException e) {
			log.info("User confirmation error. " + e.getMessage());
			response.addError("User confirmation error. " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	/**
	 * 
	 * Creates a new user for a specific application.
	 * @throws InvalidRecaptchaException  
	 * @throws ReCaptchaInvalidException 
	 * 
	 */
	@PostMapping
	public ResponseEntity<Response<?>> createUser(@Valid @RequestBody UserDto userDto, 
			HttpServletRequest request) throws InvalidRecaptchaException, ReCaptchaInvalidException {
		
		String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		captchaService.processResponse(recaptchaResponse);
		
		log.info("Creating user {}", userDto.getUserName());
		Response<TokenDto> response = new Response<>();
		
		try {
			// generate a token with all user information
			User user = DTOUtils.userDtoToUser(userDto);
			//response.setData(DTOUtils.userToUserDTO(user, userDto.getApplication()));
			String token = jwtTokenUtil.createRegistrationToken(user, userDto.getApplication());
			// Send back the generated token by email
			EmailMessage em = new EmailMessage();
			String url = userConfirmationViewUrl + "?token=" + token;
			em.setTitle("Confirmation Email");
			em.setText("Please, click the confirmation link to confirm your email and sign "
					+ "into your NOTES account. " + url);
			em.setFrom("noreply@notes.jovanibrasil.com");
			em.setTo(user.getEmail());
			em.setTextType("text/plain");
			em.setTitle("NOTES - Email Confirmation");
			integration.sendEmail(em);
						
			return ResponseEntity.ok(response);			
		} catch (Exception e) {//UserServiceException e) {	
//			log.error("Save user error {}", e.getErrorMessages());
//			e.getErrorMessages().forEach(err -> response.addError(err));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
	}

	/**
	 * Updates a specified user.
	 * 
	 */
	@PutMapping
	public ResponseEntity<Response<UserDto>> updateUser(@Valid @RequestBody UserDto userDto, HttpServletRequest request){
		
		log.info("Update user {}", userDto.getUserName());
		Response<UserDto> response = new Response<>();
		
		try {
			User user = this.userService.updateUser(DTOUtils.userDtoToUser(userDto));
			response.setData(DTOUtils.userToUserDTO(user, userDto.getApplication()));
			return ResponseEntity.ok(response);
		} catch (UserServiceException e) {	
			log.error("Update user error {}", e.getErrorMessages());
			e.getErrorMessages().forEach(err -> response.addError(err));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
	}
	
	
	/**
	 * Deletes an user by name.
	 * 
	 * This endpoint is accessible only for local services. Please, see the security configuration.
	 * 
	 * @param userName is the name of the user that you want to delete.
	 * @return ...
	 * 
	 */
	@DeleteMapping(value="/{username}")
	public ResponseEntity<Response<String>> deleteUser(@PathVariable("username") String userName){
		
		log.info("Delete user {}", userName);
		Response<String> response = new Response<>();
		
		try {
			this.userService.deleteUser(userName);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		} catch (UserServiceException e) {
			log.error("Delete user error {}", e.getErrorMessages());
			e.getErrorMessages().forEach(err -> response.addError(err));
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	
}
