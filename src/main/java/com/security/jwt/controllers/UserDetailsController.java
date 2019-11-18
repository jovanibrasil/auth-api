package com.security.jwt.controllers;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.jwt.dto.DTOUtils;
import com.security.jwt.dto.UserDetailsDTO;
import com.security.jwt.dto.CreateUserDTO;
import com.security.jwt.entities.User;
import com.security.jwt.exceptions.implementations.UserServiceException;
import com.security.jwt.response.Response;
import com.security.jwt.services.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/userdetails")
public class UserDetailsController {
		
	private static final Logger log = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Accessible only for local services
	 */
	@PutMapping
	public ResponseEntity<Response<CreateUserDTO>> updateUserDetails(@Valid @RequestBody UserDetailsDTO userDto, 
			HttpServletRequest request, @RequestHeader(name="Accept-Language", required = false) Locale locale){
		
		log.info("Update user");
		Response<CreateUserDTO> response = new Response<>();
		Optional<User> optUser = this.userService.findByUserName(userDto.getUserName());
		
		if(!optUser.isPresent()) {
			log.error("User not found.");
			response.addError(messageSource.getMessage("user.not.found", null, locale));
			return ResponseEntity.badRequest().body(response);
		}
		
		User currentUser = optUser.get();
		
		try {
			this.userService.save(currentUser);
		} catch (UserServiceException e) {
			e.printStackTrace();
		}
		response.setData(DTOUtils.userToUserDTO(currentUser, userDto.getMyApplications().get(0)));
		return ResponseEntity.ok(response);
	}
		
}
