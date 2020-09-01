package com.security.web.exception.implementation;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4717288705305477180L;

	public NotFoundException(String message) {
        super(message);
    }
}
