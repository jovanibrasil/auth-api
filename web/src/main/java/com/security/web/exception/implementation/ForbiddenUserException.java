package com.security.web.exception.implementation;

public class ForbiddenUserException extends RuntimeException {

	private static final long serialVersionUID = -2736830408545581111L;

	public ForbiddenUserException(String string) {
		super(string);
	}

}
