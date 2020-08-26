package com.security.web.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.jwt.model.enums.ProfileEnum;
import com.security.jwt.util.JwtUserFactory;
import com.security.jwt.util.PasswordUtils;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.domain.UserStatus;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.exception.implementation.NotFoundException;
import com.security.web.exception.implementation.ValidationException;
import com.security.web.repository.UserRepository;
import com.security.web.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final IntegrationServiceImpl integrationService;
	private final RabbitTemplate rabbitTemplate;
	private final UserMapper userMapper;
	private final JwtTokenGenerator tokenGenerator;
	
	public UserServiceImpl(UserRepository userRepository, 
			RabbitTemplate rabbitTemplate,
			@Lazy IntegrationServiceImpl integrationService,
			UserMapper userMapper,
			JwtTokenGenerator tokenGenerator) {
		this.userRepository = userRepository;
		this.rabbitTemplate = rabbitTemplate;
		this.integrationService = integrationService;
		this.userMapper = userMapper;
		this.tokenGenerator = tokenGenerator;
	}

	@Value("${urls.notes.userconfirmationview}")
	private String userConfirmationViewUrl;

	/**
	 * Returns the user with the specified name.
	 * 
	 * @param userName
	 * @return
	 */
	@Override
	public User findUserByUserName(String userName) {
		return userRepository.findByUsername(userName).orElseThrow(() -> new NotFoundException("error.user.notfound"));
	}

	/**
	 * Returns the user with the specified email.
	 * 
	 * @param email
	 * @return
	 */
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("error.user.notfound"));
	}

	/**
	 * Deletes an user by name.
	 * 
	 * @param userName is the name of the user that you want to delete.
	 * @return
	 * 
	 */
	@Override
	public void deleteUserByName(String userName) {
		userRepository.findByUsername(userName).ifPresentOrElse(user -> {
			// remove the user for each registered application
			integrationService.deleteServiceUser(user);
			userRepository.delete(user);
		},
		() -> new NotFoundException("error.user.notfound"));
	}

	@Override
	public User findUserById(Long id) {
		return this.userRepository.findUserById(id).orElseThrow(() -> new NotFoundException("error.user.notfound"));
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
		userRepository.findByUsername(user.getUsername()).ifPresent(x -> {
			log.info("The username {} already exists.", x.getUsername());
			errors.add("error.user.name.alreadyexists");
		});

		userRepository.findByEmail(user.getEmail()).ifPresent(x -> {
			log.info("The email {} already exists.", x.getEmail());
			errors.add("error.email.alreadyexists");
		});

		if(!errors.isEmpty()) { throw new ValidationException(errors); }
		return errors;
	}

	/**
	 * Creates an user.
	 *
	 * @param userDto contains password and user name.
	 * @return
	 */
	@Transactional
	@Override
	public User saveUser(User user) {
		log.info("Saving user: {}", user);
		// Validate if user name and email already exists
		validateUser(user);
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		user.setSignUpDateTime(LocalDateTime.now());
		user.setProfile(ProfileEnum.ROLE_USER);
		user.getRegistries().get(0).setUserStatus(UserStatus.CREATED);
		user = userRepository.save(user);
		
		String registrationToken = tokenGenerator
				.createRegistrationToken(user.getUsername(), user.getRegistries().get(0).getApplication().getType());
		
		try {
			integrationService.createServiceUser(user);
			UserDTO userDTO = userMapper.userToUserDto(user);
			
			userDTO.setApplication(user.getRegistries().get(0).getApplication().getType());
			rabbitTemplate.convertAndSend("user-created", userDTO, message -> {
					message.getMessageProperties()
						.getHeaders()
						.put("registrationToken", registrationToken);
					return message;
				}
			);
		} catch (Exception e) {
			log.info("Communication error. {}", e.getMessage());
			userRepository.delete(user);
		}
		
		return user;
	}
	
	@Transactional
	@Override
	public void confirmUser(String confirmationToken) {
		String userName = tokenGenerator.getUserNameFromToken(confirmationToken);
		String appName = tokenGenerator.getApplicationName(confirmationToken);
		log.info("Confirming user {} to application {}", userName, appName);
		userRepository.findByUsername(userName).ifPresent(user -> {
			Registry registry = user.getRegistries().stream()
				.filter(r ->  r.getApplication().getType().toString().equals(appName) )
				.findFirst()
				.get();
			log.info("Changing registry status from {} to {}", 
					registry.getUserStatus(), UserStatus.ACTIVATED);
			registry.setUserStatus(UserStatus.ACTIVATED);
		});
	}

	/**
	 * Updates a specified user. Valid password for authentication is required.
	 * 
	 */
	@Transactional
	@Override
	public User updateUser(User user) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Updating user {}", userName);
		User currentUser = findUserByUserName(userName);
		currentUser.setPassword(PasswordUtils.generateHash(user.getPassword()));
		userRepository.save(currentUser);
		return currentUser;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		log.info("Searching for " + username);
		User user = findUserByUserName(username);
		return JwtUserFactory.create(user.getUsername(),
				user.getPassword(), user.getProfile());
	}

}
