package com.security.web.exceptions.handlers;

import com.security.jwt.exceptions.TokenException;
import com.security.web.exceptions.implementations.*;
import com.security.web.exceptions.entities.ErrorDetail;
import com.security.web.exceptions.entities.Response;
import com.security.web.exceptions.entities.ValidationError;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private final MessageSource messageSource;

	/**
	 * Handles invalid arguments exception.
	 *
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<ValidationError> fieldErrors = ex.getBindingResult().getFieldErrors()
				.stream().map(e -> new ValidationError(e.getDefaultMessage(), e.getField(), e.getRejectedValue()))
				.collect(Collectors.toList());

		ErrorDetail error = ErrorDetail.builder()
				.message("Invalid field values")
				.code(status.value())
				.status(status.getReasonPhrase())
				.objectName(ex.getBindingResult().getObjectName())
				.errors(fieldErrors).build();

		Response<Object> response = new Response<Object>();
		response.addError(error);
		return ResponseEntity.unprocessableEntity().body(response);

	}

	@ExceptionHandler(MicroServiceIntegrationException.class)
	public ResponseEntity<Response<?>> handleServiceIntegrationException(MicroServiceIntegrationException microserviceIntegrationException){
//		log.info("The required application server is not responding. {}",  e.getMessage());
//		response.addError(msgSrc.getMessage("error.app.notresponding", locale));
//		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);

		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = ErrorDetail.builder()
				.message(microserviceIntegrationException.getMessage())
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex, Locale locale){
//		log.info("It was not possible to save the user. {}",  e.getMessage());
//			response.addError("It was not possible to save the user.");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = ErrorDetail.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(TokenException.class)
	public ResponseEntity<Response<?>> handleInvalidTokenException(TokenException invalidTokenException){
//		log.info(msgSrc.getMessage("error.token.invalid", locale) + e.getMessage());
//		response.addError(msgSrc.getMessage("error.token.invalid", locale) + e.getMessage());
//		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);

		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = ErrorDetail.builder()
				.message(invalidTokenException.getMessage())
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
	}
	
	/**
	 * Handles unauthorized exception. This exception indicates that the request requires
	 * authentication or the authentication process has invalid parameters.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<Response<?>> handleUnauthorizedUserException(UnauthorizedUserException ex, Locale locale){
		Response<String> response = new Response<>();
		ErrorDetail errorDetail = ErrorDetail.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	private String getMessage(String message, Locale locale) {
		return messageSource.getMessage(message, null, locale);
	}

	/**
	 * Handles forbidden exception. The forbidden exception occurs when the server understood 
	 * the request but refused to fulfill it. Example: the user has insufficient credentials
	 * to access an specific URL. 
	 * 
	 * @return
	 */
	@ExceptionHandler(ForbiddenUserException.class)
	public ResponseEntity<Response<?>> handleForbiddenUserException(ForbiddenUserException ex, Locale locale){
		Response<String> response = new Response<String>();
		ErrorDetail errorDetail = ErrorDetail.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		response.addError(errorDetail);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
	
	
}
