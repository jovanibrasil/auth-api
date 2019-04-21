package com.jwt.security.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import com.jwt.utils.ApplicationType;
import com.validators.EnumListValidator;

public class UserDetailsDTO {

	@NotBlank(message="Username must not be blank or null.")
	private String userName;
	
	@NonNull @EnumListValidator(enumClass=ApplicationType.class)
	private List<ApplicationType> applications;

	public UserDetailsDTO(String application, List<ApplicationType> applications) {
		super();
		this.applications = applications;
	}

	public List<ApplicationType> getMyApplications() {
		return applications;
	}

	public void setMyApplications(List<ApplicationType> applications) {
		this.applications = applications;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
