package com.jwt;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.repositories.UserRepository;
import com.jwt.utils.PasswordUtils;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

//	@Autowired
//	private UserRepository userRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			
//			User user = new User();
//			user.setEmail("user@email.com");
//			user.setUserName("user");
//			user.setProfile(ProfileEnum.ROLE_USER);
//			user.setSignUpDate(new Date());
//			user.setMyApplications(Arrays.asList("NOTES_APP"));
//			user.setPassword(PasswordUtils.generateHash("123456"));
//			this.userRepository.save(user);
//			
//			User admin = new User();
//			admin.setEmail("blog.admin@email.com");
//			admin.setUserName("admin");
//			admin.setProfile(ProfileEnum.ROLE_ADMIN);
//			admin.setSignUpDate(new Date());
//			admin.setMyApplications(Arrays.asList("BLOG_APP"));
//			admin.setPassword(PasswordUtils.generateHash("123456"));
//			this.userRepository.save(admin);
			
		};
	}
	
}
