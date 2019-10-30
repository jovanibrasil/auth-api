package com.security.jwt.exceptions.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.security.jwt.exceptions.entities.ErrorDetail;
import com.security.jwt.exceptions.entities.Response;
import com.security.jwt.exceptions.entities.ValidationError;
import com.security.jwt.exceptions.implementations.ForbiddenUserException;
import com.security.jwt.exceptions.implementations.UnauthorizedUserException;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	/**
	 * Handles invalid arguments exception.
	 * 
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
				.stream().map(e -> new ValidationError(e.getDefaultMessage(), e.getField(), e.getRejectedValue()))
				.collect(Collectors.toList());
		ErrorDetail error = new ErrorDetail.Builder()
				.message("Invalid field values")
				.code(status.value())
				.status(status.getReasonPhrase())
				.objectName(ex.getBindingResult().getObjectName())
				.errors(errors).build();
		Response<Object> response = new Response<Object>();
		response.addError(error);
		return ResponseEntity.unprocessableEntity().body(response);
		
	}
	
	/**
	 * Handles unauthorized exception. This exception indicates that the request requires
	 * authentication or the authentication process has invalid parameters.
	 * 
	 * @param untException
	 * @return
	 */
	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<Response<?>> handleUnauthorizedUserException(UnauthorizedUserException untException){
		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = new ErrorDetail.Builder()
				.message(untException.getMessage())
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
	
	/**
	 * Handles forbidden exception. The forbidden exception occurs when the server understood 
	 * the request but refused to fulfill it. Example: the user has insufficient credentials
	 * to access an specific URL. 
	 * 
	 * @param fbdException
	 * @return
	 */
	@ExceptionHandler(ForbiddenUserException.class)
	public ResponseEntity<Response<?>> handleForbiddenUserException(ForbiddenUserException fbdException){
		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = new ErrorDetail.Builder()
				.message(fbdException.getMessage())
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
	
	
}
