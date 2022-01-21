package org.urbanenvironmentmonitor.shared.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.urbanenvironmentmonitor.security.auth.exceptions.InvalidCredentialsException;

import java.util.Locale;

@ControllerAdvice
public class SharedControllerAdvice
{
	private final MessageSource messageSource;

	public SharedControllerAdvice(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String code, Object[] params,
	                                                          Locale locale)
	{
		ErrorResponse errorResponse = new ErrorResponse(
				status.value(),
				messageSource.getMessage(code, params, locale),
				code,
				params
		);

		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e, Locale locale)
	{
		e.printStackTrace();
		return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", null, locale);
	}

	@ExceptionHandler({InvalidCredentialsException.class})
	public ResponseEntity<ErrorResponse> invalidCredentialsExceptionHandler(Exception e, Locale locale)
	{
		return createErrorResponse(HttpStatus.UNAUTHORIZED, "invalid_credentials", null, locale);
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
		private Object[] messageParams;
	}
}
