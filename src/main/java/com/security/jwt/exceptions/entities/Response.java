package com.security.jwt.exceptions.entities;

import java.util.ArrayList;
import java.util.List;

public class Response<T> {

	private T data;
	private List<ErrorDetail> errors;
	
	public Response() {}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<ErrorDetail> getErrors() {
		if(errors == null)
			errors = new ArrayList<>();
		return errors;
	}	
	
	public void addError(ErrorDetail error) {
		getErrors().add(error);
	}

}
