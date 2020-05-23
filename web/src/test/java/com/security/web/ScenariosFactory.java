package com.security.web;

import java.time.LocalDateTime;

import com.security.jwt.model.enums.ProfileEnum;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;

public class ScenariosFactory {

	public static User createUser() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setUsername("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		return user;
	}

	public static Application createBlogApplication() {
		return new Application(ApplicationType.BLOG_APP);
	}

	public static Application createNotesApplication() {
		return new Application(ApplicationType.NOTES_APP);
	}

	
}
