package com.security.jwt.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.exceptions.implementations.UserServiceException;
import com.security.jwt.integration.Integration;
import com.security.jwt.repositories.UserRepository;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.PasswordUtils;

@Slf4j
@Service
@Primary
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Integration integrationService;
	
	@Override
	public Optional<User> findByUserName(String userName) {
		return Optional.ofNullable(this.userRepository.findUserByUserName(userName));
	}

	/**
	 * 
	 * Validate if user name and email already exists.
	 * 
	 * @param user
	 * @return
	 */
	public List<String> validateUser(User user){
		List<String> errors = new ArrayList<>();
		this.findByUserName(user.getUserName()).ifPresent(x -> {
			log.info("The username {} already exists.", x.getUserName());
			errors.add("This username already exists.");
		});
		this.findUserByEmail(user.getEmail()).ifPresent(x -> {
			log.info("The email {} already exists.", x.getEmail());
			errors.add("This email already exists.");
		});
		return errors;
	}
		
	@Override
	public User save(User user) throws UserServiceException {
		// Validate if user name and email already exists
		List<String> errors = validateUser(user);
		if(!errors.isEmpty()) {
			throw new UserServiceException(errors);
		}	
		user.setSignUpDateTime(LocalDateTime.now());
		user.setProfile(ProfileEnum.ROLE_USER);
		user = this.userRepository.save(user);
		
		try {
			integrationService.createServiceUser(user);
			return user;
		} catch (Exception e) {
			log.info("Integration service error. {}", e.getMessage());
			throw new UserServiceException("Integration service error. " + e.getMessage()); 
		}
	}
	
	public User updateUser(User user) throws UserServiceException {
		
		Optional<User> optUser = this.findUserById(user.getId());
		List<String> errors = new ArrayList<>();
		User currentUser = optUser.get();
		
		if(!optUser.isPresent()) {
			log.error("User not found.");
			throw new UserServiceException(Arrays.asList("User not found."));
		}		
		// Apply validations to the user. Validates if the user name and email already exist.
		if (!currentUser.getEmail().equals(user.getEmail())) {
			this.findUserByEmail(user.getEmail()).ifPresent(x -> errors.add("This email already exists."));
			currentUser.setEmail(user.getEmail());
		}
		if (!currentUser.getUserName().equals(user.getUserName())) {
			this.findByUserName(user.getUserName()).ifPresent(x -> errors.add("This username already exists."));
			currentUser.setUserName(user.getUserName());
		}
		
		// Validate if user name and email already exists
		if(!errors.isEmpty()) {
			throw new UserServiceException(errors);
		}
		currentUser.setPassword(PasswordUtils.generateHash(user.getPassword()));
		
		this.userRepository.save(currentUser);
		return currentUser;
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		return Optional.ofNullable(this.userRepository.findUserByEmail(email));
	}

	@Override
	public void deleteUser(String userName) throws UserServiceException {
		Optional<User> optUser = this.findByUserName(userName);
		
		if(optUser.isPresent()) {
			this.userRepository.delete(optUser.get());
			// remove the user for each registered application
			integrationService.deleteServiceUser(optUser.get());
		}else {
			throw new UserServiceException("The user does not exist.");
		}
	}

	@Override
	public Optional<User> findUserById(Long id) {
		return Optional.ofNullable(this.userRepository.findUserById(id));
	}
	
	
}
