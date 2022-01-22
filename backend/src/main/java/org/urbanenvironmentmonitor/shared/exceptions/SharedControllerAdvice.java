package org.urbanenvironmentmonitor.shared.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.urbanenvironmentmonitor.security.auth.exceptions.InvalidCredentialsException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class SharedControllerAdvice
{
	private final MessageSource messageSource;

	public SharedControllerAdvice(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	private ResponseEntity<ErrorResponse> createMultiErrorResponse(HttpStatus status, List<String> codes,
	                                                               Locale locale)
	{
		ErrorResponse errorResponse = new ErrorResponse(
				status.value(),
				codes.stream()
						.map(code -> new Error(messageSource.getMessage(code, null, locale), code, null))
						.collect(Collectors.toList())
		);

		return new ResponseEntity<>(errorResponse, status);
	}

	private ResponseEntity<ErrorResponse> createSingleErrorResponse(HttpStatus status, String code, Object[] params,
	                                                                Locale locale)
	{
		ErrorResponse errorResponse = new ErrorResponse(
				status.value(),
				List.of(new Error(
						messageSource.getMessage(code, params, locale),
						code,
						params
				))
		);

		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e, Locale locale)
	{
		e.printStackTrace();
		return createSingleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", null, locale);
	}

	@ExceptionHandler({InvalidCredentialsException.class})
	public ResponseEntity<ErrorResponse> invalidCredentialsExceptionHandler(Exception e, Locale locale)
	{
		return createSingleErrorResponse(HttpStatus.UNAUTHORIZED, "invalid_credentials", null, locale);
	}

	@ExceptionHandler({AccessDeniedException.class})
	public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(Exception e, Locale locale)
	{
		return createSingleErrorResponse(HttpStatus.FORBIDDEN, "access_denied", null, locale);
	}

	@ExceptionHandler({ServerWebInputException.class})
	public ResponseEntity<ErrorResponse> serverWebInputExceptionHandler(ServerWebInputException e, Locale locale)
	{
		return createSingleErrorResponse(HttpStatus.BAD_REQUEST, "bad_request", null, locale);
	}

	@ExceptionHandler({WebExchangeBindException.class})
	public ResponseEntity<ErrorResponse> webExchangeBindExceptionHandler(WebExchangeBindException e, Locale locale)
	{
		List<String> errorMessages = e.getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return createMultiErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, locale);
	}

	@Data
	@AllArgsConstructor
	private static class Error
	{
		/**
		 * The readable message with expanded parameters in the locale of the server.
		 */
		private String message;

		/**
		 * The code of the error message to be translated in the frontend.
		 */
		private String messageCode;

		/**
		 * The params required for expanding during translation in the frontend.
		 */
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Object[] messageParams;
	}

	@Data
	@AllArgsConstructor
	private static class ErrorResponse
	{
		/**
		 * HttpStatus of the error.
		 */
		private int status;

		/**
		 * The list of errors that occurred.
		 */
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<Error> errors;
	}
}
