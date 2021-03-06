package com.security.web.exception.handler;

import com.security.jwt.exception.TokenException;
import com.security.web.domain.dto.ErrorDetailDTO;
import com.security.web.domain.dto.ValidationError;
import com.security.web.exception.implementation.ForbiddenUserException;
import com.security.web.exception.implementation.MicroServiceIntegrationException;
import com.security.web.exception.implementation.NotFoundException;
import com.security.web.exception.implementation.UnauthorizedUserException;
import com.security.web.exception.implementation.ValidationException;

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
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<ValidationError> fieldErrors = ex.getBindingResult().getFieldErrors()
				.stream().map(e -> new ValidationError(e.getDefaultMessage(), e.getField(), e.getRejectedValue()))
				.collect(Collectors.toList());

		ErrorDetailDTO error = ErrorDetailDTO.builder()
				.message("Invalid field values")
				.code(status.value())
				.status(status.getReasonPhrase())
				.objectName(ex.getBindingResult().getObjectName())
				.errors(fieldErrors).build();

		return ResponseEntity.badRequest().body(error);

	}

	@ExceptionHandler(MicroServiceIntegrationException.class)
	public ResponseEntity<?> handleServiceIntegrationException(MicroServiceIntegrationException microserviceIntegrationException){
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message(microserviceIntegrationException.getMessage())
				.build();
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorDetail);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException ex, Locale locale){
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetail);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> handleValidationException(ValidationException ex, Locale locale){
		List<ValidationError> validationErrors = ex.getErrorMessages().stream()
				.map(message -> new ValidationError(getMessage(message, locale), null, null))
				.collect(Collectors.toList());
		
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message("Invalid field values")
				.errors(validationErrors)
				.build();
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetail);
	}
	
	@ExceptionHandler(TokenException.class)
	public ResponseEntity<?> handleInvalidTokenException(Exception ex, Locale locale){
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetail);
	}
	
	/**
	 * Handles unauthorized exception. This exception indicates that the request requires
	 * authentication or the authentication process has invalid parameters.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<?> handleUnauthorizedUserException(UnauthorizedUserException ex, Locale locale){
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
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
	public ResponseEntity<?> handleForbiddenUserException(ForbiddenUserException ex, Locale locale){
		ErrorDetailDTO errorDetail = ErrorDetailDTO.builder()
				.message(getMessage(ex.getMessage(), locale))
				.build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
	}
	
}
