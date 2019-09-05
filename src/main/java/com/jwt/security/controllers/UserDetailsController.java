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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.response.Response;
import com.jwt.security.dto.DTOUtils;
import com.jwt.security.dto.UserDetailsDTO;
import com.jwt.security.dto.UserDto;
import com.jwt.security.entities.User;
import com.jwt.security.services.UserService;
import com.jwt.security.services.impl.UserServiceException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/userdetails")
public class UserDetailsController {
		
	private static final Logger log = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
	private UserService userService;
		
	
	/**
	 * Accessible only for local services
	 */
	@PutMapping
	public ResponseEntity<Response<UserDto>> updateUserDetails(@Valid @RequestBody UserDetailsDTO userDto, BindingResult result, HttpServletRequest request){
		
		log.info("Update user");
		Response<UserDto> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
		
		Optional<User> optUser = this.userService.findByUserName(userDto.getUserName());
		
		if(!optUser.isPresent()) {
			log.error("User not found.");
			response.addError("User not found.");
			return ResponseEntity.badRequest().body(response);
		}
		
		User currentUser = optUser.get();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		try {
			this.userService.save(currentUser);
		} catch (UserServiceException e) {
			e.printStackTrace();
		}
		response.setData(DTOUtils.userToUserDTO(currentUser, userDto.getMyApplications().get(0)));
		return ResponseEntity.ok(response);
	}
		
}
