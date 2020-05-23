package com.security.web.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetailDTO {

	private String message;
    private int code;
    private String status;
    private String objectName;
    private List<ValidationError> errors;

}
