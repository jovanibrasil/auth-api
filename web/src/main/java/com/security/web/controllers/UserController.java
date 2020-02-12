package com.security.web.controllers;

import com.security.captcha.CaptchaService;
import com.security.captcha.InvalidRecaptchaException;
import com.security.captcha.ReCaptchaInvalidException;
import com.security.web.domain.User;
import com.security.web.dto.*;
import com.security.web.mappers.UserMapper;
import com.security.web.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final CaptchaService captchaService;
	private final UserMapper userMapper;

	/**
	 * Returns the user with the specified name.
	 * 
	 * @param userName
	 * @return
	 */
	@GetMapping("/{userName}")
	public ResponseEntity<Response<CreateUserDTO>> getUser(@PathVariable String userName) {
		
		User user = userService.findByUserName(userName);
		CreateUserDTO userDto = new CreateUserDTO();
		userDto.setEmail(user.getEmail());
		userDto.setUserName(user.getUserName());
		
		return ResponseEntity.ok(new Response<>(userDto));
	}
	
	/**
	 * Creates an user.
	 *
	 * @param userDto contains password and user name.
	 * @return
	 * @throws ReCaptchaInvalidException
	 * @throws InvalidRecaptchaException
	 */
	@PostMapping
	public ResponseEntity<Response<?>> createUser(@Valid @RequestBody RegistrationUserDTO userDto, HttpServletRequest request)
			throws InvalidRecaptchaException, ReCaptchaInvalidException{
		log.info("User registration");
		String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		captchaService.processResponse(recaptchaResponse);

		User user = userMapper.registrationUserDtoToUser(userDto);
		user = userService.saveUser(user);

		// Set the resource location and return 201 Created
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(user.getId())
				.toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_LOCATION, location.toString());
		return ResponseEntity.status(HttpStatus.CREATED)
				.headers(headers).body(new Response<>());
	}
	
	/**
	 * 
	 * Generates a verification email with a token. The token contains user information like email and
	 * application name.
	 * 
	 * @param userDto contains email address and application name. The email address must be unique.
	 *
	 * @throws InvalidRecaptchaException  
	 * @throws ReCaptchaInvalidException 
	 * 
	 */
	@PostMapping("/confirmation")
	public ResponseEntity<Response<?>> confirmUserEmail(@Valid @RequestBody ConfirmUserDTO userDto,
			HttpServletRequest request) throws InvalidRecaptchaException, ReCaptchaInvalidException {

		log.info("Creating confirmation token for the email {}", userDto.getEmail());

		String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		captchaService.processResponse(recaptchaResponse);

		userService.confirmUserEmail(userMapper.confirmUserDtoToUser(userDto), userDto.getApplication());
		return ResponseEntity.ok(new Response<>());
	}

	/**
	 * Updates a specified user. Valid password for authentication is required.
	 * 
	 */
	@PutMapping
	public ResponseEntity<Response<?>> updateUser(@Valid @RequestBody UpdateUserDTO userDto){
		User user = userMapper.updateUserDtoToUser(userDto);
		user = userService.updateUser(user);
		return ResponseEntity.ok().body(new Response<>());
	}

	/**
	 * Deletes an user by name.
	 * 
	 * This endpoint is accessible only for services. Please, see the security configuration.
	 * 
	 * @param userName is the name of the user that you want to delete.
	 * @return
	 * 
	 */
	@DeleteMapping(value="/{username}")
	public ResponseEntity<Response<String>> deleteUser(@PathVariable("username") String userName){
		log.info("Delete user {}", userName);
		userService.deleteUser(userName);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Response<>());
	}
	
	
}
