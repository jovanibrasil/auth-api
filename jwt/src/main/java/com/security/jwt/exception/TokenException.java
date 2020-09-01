package com.security.jwt.exception;

public class TokenException extends RuntimeException {

	private static final long serialVersionUID = 6524809828758061275L;

	public TokenException(String message) {
		super(message);
	}
	
}
