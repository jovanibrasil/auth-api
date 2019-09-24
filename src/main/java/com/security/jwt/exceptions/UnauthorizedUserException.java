package com.security.jwt.exceptions;

public class UnauthorizedUserException extends RuntimeException {

	private static final long serialVersionUID = -6352177626556512592L;

	public UnauthorizedUserException(String string) {
		super(string);
	}
	
}
