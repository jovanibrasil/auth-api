package com.security.jwt.exceptions.implementations;

public class InvalidTokenException extends Exception {

	private static final long serialVersionUID = 6524809828758061275L;

	public InvalidTokenException(String message) {
		super(message);
	}
	
}
