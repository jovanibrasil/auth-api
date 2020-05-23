package com.security.web.controller;

import java.net.URI;

import javax.validation.Valid;

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

import com.jovanibrasil.captcha.aspect.Recaptcha;
import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;
	
	/**
	 * Returns the user with the specified name.
	 * 
	 * @param userName
	 * @return
	 */
	@GetMapping("/{userName}")
	public ResponseEntity<UserDTO> getUser(@PathVariable String userName) {
		User user = userService.findUserByUserName(userName);
		return ResponseEntity.ok(userMapper.userToUserDto(user));
	}
	
	/**
	 * Returns the user with the specified email.
	 * 
	 * @param email
	 * @return
	 */
	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam(required = true) String email) {
		User user = userService.findUserByEmail(email);
		return ResponseEntity.ok(userMapper.userToUserDto(user));
	}
	
	
	/**
	 * Creates an user.
	 *
	 * @param userDto contains password and user name.
	 * @return
	 */
	@Recaptcha
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm) {
		log.info("User registration");

		User user = userService.saveUser(userMapper.userFormToUser(userForm));

		// Set the resource location and return 201 Created
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{userName}")
				.buildAndExpand(user.getUsername())
				.toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	/**
	 * Updates a specified user. Valid password for authentication is required.
	 * 
	 */
	@Recaptcha
	@PutMapping
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UpdateUserForm userDto){
		User user = userService.updateUser(userMapper.updateUserDtoToUser(userDto));
		return ResponseEntity.ok().body(userMapper.userToUserDto(user));
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
	@DeleteMapping("/{username}")
	public ResponseEntity<?> deleteUser(@PathVariable("username") String userName){
		log.info("Delete user {}", userName);
		userService.deleteUserByName(userName);
		return ResponseEntity.noContent().build();
	}
	
}
