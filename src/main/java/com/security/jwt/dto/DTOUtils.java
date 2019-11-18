package com.security.jwt.dto;

import java.util.Arrays;

import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.utils.ApplicationType;

public class DTOUtils {

	public static CreateUserDTO userToUserDTO(User user, ApplicationType applicationType) {
		CreateUserDTO userDTO = new CreateUserDTO();
		userDTO.setUserName(user.getUserName());
		userDTO.setId(user.getId());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(null);
		userDTO.setApplication(applicationType);
		return userDTO;
	}

	public static User userDtoToUser(CreateUserDTO userDTO) {
		User user = new User();
		user.setUserName(userDTO.getUserName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setRegistries(Arrays.asList(new Registry(new Application(userDTO.getApplication()), user)));
		return user;
	}	
	
	
}
