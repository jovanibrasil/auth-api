package com.security.jwt.exceptions.implementations;

import java.util.Arrays;
import java.util.List;

public class UserServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1755385273056791890L;

	List<String> errorMessages;
	
	public UserServiceException(List<String> errorList) {
		super(errorList.toString());
		this.errorMessages = errorList;
	}
	
	public UserServiceException(String errorMessage) {
		super(errorMessage);
		this.errorMessages = Arrays.asList(errorMessage);
	}
	
	public List<String> getErrorMessages(){
		return this.errorMessages;
	}
	
}
