package com.security.web.exceptions.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
