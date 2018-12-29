package com.jwt.response;

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
	
	public Response() {}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<String> getErrors() {
		if(errors == null)
			errors = new ArrayList<>();
		return errors;
	}	
	
}
