package com.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.repositories.UserRepository;
import com.jwt.utils.PasswordUtils;

@SpringBootApplication
public class Application {

	@Autowired
	private UserRepository userRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			
			User user = new User();
			user.setEmail("user@email.com");
			user.setProfile(ProfileEnum.ROLE_USER);
			user.setPassword(PasswordUtils.generateHash("123456"));
			this.userRepository.save(user);
			
			User admin = new User();
			admin.setEmail("admin@email.com");
			admin.setProfile(ProfileEnum.ROLE_ADMIN);
			admin.setPassword(PasswordUtils.generateHash("123456"));
			this.userRepository.save(admin);
			
		};
	}
	
}
