package com.security.jwt.controllers;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.security.jwt.dto.ConfirmUserDTO;
import com.security.jwt.dto.DTOUtils;
import com.security.jwt.dto.RegistrationUserDTO;
import com.security.jwt.dto.TokenDto;
import com.security.jwt.dto.UserDto;
import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.exceptions.UserServiceException;
import com.security.jwt.integration.EmailMessage;
import com.security.jwt.integration.Integration;
import com.security.jwt.response.Response;
import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.ApplicationType;
import com.security.jwt.utils.PasswordUtils;
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
	
	/**
	 * Creates the user.
	 * 
	 * @param token contains the user email and application name
	 * @param userDto contains password and user name.
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Response<?>> createUser(@Valid @RequestBody RegistrationUserDTO userDto){
		log.info("User registration");
		Response<?> response = new Response<>();
		try {
			// Get user informations from token
			String email = jwtTokenUtil.getEmailFromToken(userDto.getToken());
			String applicationName = jwtTokenUtil.getApplicationName(userDto.getToken());
			
			// TODO if user information are wrong, return error
			User user = new User(userDto.getUserName(), email, PasswordUtils.generateHash(userDto.getPassword()));
			Application application = new Application(ApplicationType.valueOf(applicationName));
			user.setRegistries(Arrays.asList(new Registry(application, user)));
			log.info("Saving user: [Name: {} Email: {} Application: {}]", 
					user.getUserName(), user.getEmail(), applicationName);
			user = this.userService.save(user);
			
			// Set the resource location and return 201 Created
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
	 * Generates a email verification with a token. The token contains user information like email and 
	 * application name. The email must be unique.
	 * 
	 * @param userDto contains email and application name
	 *
	 * @throws InvalidRecaptchaException  
	 * @throws ReCaptchaInvalidException 
	 * 
	 */
	@PostMapping("/confirmation")
	public ResponseEntity<Response<?>> confirmUserEmail(@Valid @RequestBody ConfirmUserDTO userDto, 
			HttpServletRequest request) throws InvalidRecaptchaException, ReCaptchaInvalidException {
		
		String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		captchaService.processResponse(recaptchaResponse);
		
		log.info("Creating confirmation token for the email {}", userDto.getEmail());
		Response<TokenDto> response = new Response<>();
		
		try {
			// generate a token with basic user information
			String token = jwtTokenUtil.createRegistrationToken(userDto.getEmail() ,userDto.getApplication());
			// Send back the generated token by email
			EmailMessage em = new EmailMessage();
			String url = userConfirmationViewUrl + "?token=" + token;
			em.setTitle("Confirmation Email");
			em.setText("Please, click the confirmation link to confirm your email and sign "
					+ "into your NOTES account. " + url);
			em.setFrom("noreply@notes.jovanibrasil.com");
			em.setTo(userDto.getEmail());
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
