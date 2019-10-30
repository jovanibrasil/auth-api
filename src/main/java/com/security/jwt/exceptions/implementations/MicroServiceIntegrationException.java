package com.security.jwt.exceptions.implementations;

public class MicroServiceIntegrationException extends RuntimeException {
	
	private static final long serialVersionUID = -2913054083195405274L;

	public MicroServiceIntegrationException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
	
}
