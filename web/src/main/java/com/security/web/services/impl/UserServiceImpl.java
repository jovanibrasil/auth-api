package com.security.web.services.impl;

import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.jwt.utils.PasswordUtils;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.web.dto.EmailMessage;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.NotFoundException;
import com.security.web.exceptions.implementations.ValidationException;
import com.security.web.repositories.UserRepository;
import com.security.web.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final IntegrationServiceImpl integrationService;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenGenerator jwtTokenUtil;
	@Value("${urls.notes.userconfirmationview}")
	private String userConfirmationViewUrl;

	@Override
	public User findByUserName(String userName) {
		Optional<User> optUser = this.userRepository.findByUserName(userName);
		if(!optUser.isPresent()) throw new NotFoundException("error.user.notfound");
		return optUser.get();
	}

	@Override
	public User findUserByEmail(String email) {
		Optional<User> optUser = this.userRepository.findByEmail(email);
		if(!optUser.isPresent()) throw new NotFoundException("error.user.notfound");
		return optUser.get();
	}

	@Override
	public boolean existUserWithUserName(String userName) {
		return this.userRepository.findByUserName(userName).isPresent();
	}

	@Override
	public boolean existUserWithEmail(String email) {
		return this.userRepository.findByEmail(email).isPresent();
	}

	@Override
	public void deleteUser(String userName) {
		User user = this.findByUserName(userName);
		this.userRepository.delete(user);
		// remove the user for each registered application
		integrationService.deleteServiceUser(user);
	}

	@Override
	public User findUserById(Long id) {
		Optional<User> optUser = this.userRepository.findUserById(id);
		if(!optUser.isPresent()) throw new NotFoundException("error.user.notfound");
		return optUser.get();
	}

	/**
	 * 
	 * Validate if user name and email already exists.
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public List<String> validateUser(User user){
		List<String> errors = new ArrayList<>();
		userRepository.findByUserName(user.getUserName()).ifPresent(x -> {
			log.info("The username {} already exists.", x.getUserName());
			errors.add("error.username.alreadyexists");
		});

		userRepository.findByEmail(user.getEmail()).ifPresent(x -> {
			log.info("The email {} already exists.", x.getEmail());
		errors.add("error.email.alreadyexists");
		});

		if(!errors.isEmpty()) { throw new ValidationException(errors); }
		return errors;
	}

	@Override
	public void confirmUserEmail(User user, ApplicationType applicationType) {
		// generate a token with basic user information
		String token = jwtTokenUtil
				.createRegistrationToken(user.getEmail(), applicationType);
		// Send back the generated token by email
		EmailMessage em = new EmailMessage();
		String url = userConfirmationViewUrl + "?token=" + token;
		em.setTitle("Confirmation Email");
		em.setText("Please, click the confirmation link to confirm your email and sign "
				+ "into your NOTES account. " + url);
		em.setFrom("noreply@notes.jovanibrasil.com");
		em.setTo(user.getEmail());
		em.setTextType("text/plain");
		em.setTitle("NOTES - Email Confirmation");
		integrationService.sendEmail(em);
	}

	@Transactional
	@Override
	public User saveUser(User user) {
		log.info("Saving user: {}", user);
		// Validate if user name and email already exists
		validateUser(user);
		user.setSignUpDateTime(LocalDateTime.now());
		user.setProfile(ProfileEnum.ROLE_USER);
		user = this.userRepository.save(user);
		integrationService.createServiceUser(user);
		return user;
	}

	@Override
	public boolean authenticate(String userName, String password){
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userName, password));
		return auth.isAuthenticated();
	}

	@Transactional
	@Override
	public User updateUser(User user) {

		if(!authenticate(user.getUserName(), user.getPassword())) throw new ForbiddenUserException("");

		User currentUser = findByUserName(user.getUserName());
		List<String> errors = new ArrayList<>();

		// Apply validations to the user. Validates if the user name and email already exist.
		if (!currentUser.getEmail().equals(user.getEmail())) {
			userRepository.findByEmail(user.getEmail())
					.ifPresent(x -> errors.add("error.email.alreadyexists"));
			currentUser.setEmail(user.getEmail());
		}

		if(!errors.isEmpty()) throw new ValidationException(errors);

		currentUser.setPassword(PasswordUtils.generateHash(user.getPassword()));
		this.userRepository.save(currentUser);
		return currentUser;
	}



}
