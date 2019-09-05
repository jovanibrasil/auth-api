package com.jwt.security.controllers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.response.Response;
import com.jwt.security.dto.DTOUtils;
import com.jwt.security.dto.UserDto;
import com.jwt.security.entities.Registry;
import com.jwt.security.entities.User;
import com.jwt.security.services.UserService;
import com.jwt.security.services.impl.UserServiceException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {
		
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
		
	
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
	 * 
	 * Creates a new user for a specific application.
	 * 
	 */
	@PostMapping
	public ResponseEntity<Response<UserDto>> createUser(@Valid @RequestBody UserDto userDto, BindingResult result, HttpServletRequest request){
		
		log.info("Creating user {}", userDto.getUserName());
		Response<UserDto> response = new Response<>();
		
		try {
			if(result.hasErrors()) {
				log.error("Validation error {}", result.getAllErrors());
				result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
			}
			User user = this.userService.save(DTOUtils.userDtoToUser(userDto));	
			response.setData(DTOUtils.userToUserDTO(user, userDto.getApplication()));
			return ResponseEntity.ok(response);
		} catch (UserServiceException e) {	
			log.error("Save user error {}", e.getErrorMessages());
			e.getErrorMessages().forEach(err -> response.addError(err));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
		
	}

	/**
	 * Updates a specified user.
	 * 
	 */
	@PutMapping
	public ResponseEntity<Response<UserDto>> updateUser(@Valid @RequestBody UserDto userDto, BindingResult result, HttpServletRequest request){
		
		log.info("Update user {}", userDto.getUserName());
		Response<UserDto> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
		
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
