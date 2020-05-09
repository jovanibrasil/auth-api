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
	public ResponseEntity<CreateUserDTO> getUser(@PathVariable String userName) {
		
		User user = userService.findByUserName(userName);
		CreateUserDTO userDto = new CreateUserDTO();
		userDto.setEmail(user.getEmail());
		userDto.setUserName(user.getUserName());
		
		return ResponseEntity.ok(userDto);
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
	public ResponseEntity<?> createUser(@Valid @RequestBody RegistrationUserDTO userDto, HttpServletRequest request)
			throws InvalidRecaptchaException, ReCaptchaInvalidException{
		log.info("User registration");

		User user = userMapper.registrationUserDtoToUser(userDto);
		user = userService.saveUser(user);

		// Set the resource location and return 201 Created
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(user.getId())
				.toUri();
		
		return ResponseEntity.created(uri).build();
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
	public ResponseEntity<?> confirmUserEmail(@Valid @RequestBody ConfirmUserDTO userDto,
			HttpServletRequest request) throws InvalidRecaptchaException, ReCaptchaInvalidException {

		log.info("Creating confirmation token for the email {}", userDto.getEmail());

		String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		captchaService.processResponse(recaptchaResponse);

		userService.confirmUserEmail(userMapper.confirmUserDtoToUser(userDto), userDto.getApplication());
		
		return ResponseEntity.ok().build();
	}

	/**
	 * Updates a specified user. Valid password for authentication is required.
	 * 
	 */
	@PutMapping
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO userDto){
		User user = userMapper.updateUserDtoToUser(userDto);
		user = userService.updateUser(user);
		// TODO userDto = userMapper.user
		return ResponseEntity.ok().body(userDto);
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
	public ResponseEntity<?> deleteUser(@PathVariable("username") String userName){
		log.info("Delete user {}", userName);
		userService.deleteUserByName(userName);
		return ResponseEntity.noContent().build();
	}
	
	
}
