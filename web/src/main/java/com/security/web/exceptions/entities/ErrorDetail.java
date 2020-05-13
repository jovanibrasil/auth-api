package com.security.web.exceptions.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

	private String message;
    private int code;
    private String status;
    private String objectName;
    private List<ValidationError> errors;

}
