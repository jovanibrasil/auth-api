package com.jwt.exceptions;

import java.util.List;

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
    
    private ErrorDetail(Builder builder) {
		this.message = builder.message;
		this.code = builder.code;
		this.status = builder.status;
		this.objectName = builder.objectName;
		this.errors = builder.errors;
	}

    public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	public String getObjectName() {
		return objectName;
	}

	public List<ValidationError> getErrors() {
		return errors;
	}
	

	public static class Builder {

		private String message;
	    private int code;
	    private String status;
	    private String objectName;
	    private List<ValidationError> errors;
	    
		public Builder() {}
		
		public Builder message(String message) {
			this.message = message;
			return this;
		}
		public Builder code(int code) {
			this.code = code;
			return this;
		}
		public Builder status(String status) {
			this.status = status;
			return this;
		}
		public Builder objectName(String objectName) {
			this.objectName = objectName;
			return this;
		}
		public Builder errors(List<ValidationError> errors) {
			this.errors = errors;
			return this;
		}
		public ErrorDetail build() {
			return new ErrorDetail(this);
		}

	}

}
