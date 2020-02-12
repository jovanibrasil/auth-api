package com.security.web.dto;

import java.util.ArrayList;
import java.util.List;

/*
 * This class encapsulates the response data and the error list.
 * 
 * @author Jovani Brasil
 *  
 */
public class Response<T> {

	private T data;
	private List<String> errors;
	
	public Response() {
		this.errors = new ArrayList<>();
	}

	public Response(T data) { this.data = data; }

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public void addError(String error) {
		this.errors.add(error);
	}

	public List<String> getErrors() {
		return errors;
	}	
	
}
