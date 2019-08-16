package com.jwt.security.dto;

import java.util.Arrays;

import com.jwt.security.entities.Application;
import com.jwt.security.entities.Registry;
import com.jwt.security.entities.User;
import com.jwt.utils.ApplicationType;
import com.jwt.utils.PasswordUtils;

public class DTOUtils {

	public static UserDto userToUserDTO(User user, ApplicationType applicationType) {
		UserDto userDTO = new UserDto();
		userDTO.setUserName(user.getUserName());
		userDTO.setId(user.getId());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(null);
		userDTO.setApplication(applicationType);
		return userDTO;
	}

	public static User userDtoToUser(UserDto userDTO) {
		User user = new User();
		user.setUserName(userDTO.getUserName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(PasswordUtils.generateHash(userDTO.getPassword()));
		user.setRegistries(Arrays.asList(new Registry(new Application(userDTO.getApplication()), user)));
		return user;
	}	
	
	
}
