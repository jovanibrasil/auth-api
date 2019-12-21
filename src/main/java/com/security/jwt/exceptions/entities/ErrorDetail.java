package com.security.jwt.exceptions.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDetail {

	private String message;
    private int code;
    private String status;
    private String objectName;
    private List<ValidationError> errors;

    public ErrorDetail(String message) {
		super();
		this.message = message;
	}

}
