package com.security.web.controllers;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;
import com.security.web.mappers.UserMapper;
import com.security.web.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
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
		User user = userService.findByUserName(userName);
		return ResponseEntity.ok(userMapper.userToUserDto(user));
	}
	
	/**
	 * Creates an user.
	 *
	 * @param userDto contains password and user name.
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm) {
		log.info("User registration");

		User user = userMapper.userFormToUser(userForm);
		user = userService.save(user);

		// Set the resource location and return 201 Created
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(user.getId())
				.toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	/**
	 * Updates a specified user. Valid password for authentication is required.
	 * 
	 */
	@PutMapping
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserForm userDto){
		User user = userMapper.updateUserDtoToUser(userDto);
		user = userService.update(user);
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
		userService.deleteByName(userName);
		return ResponseEntity.noContent().build();
	}
	
	
}
