package com.example.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.common.ErrorCode;
import com.example.common.ErrorResponse;
import com.example.security.exception.AuthMethodNotSupportedException;
import com.example.security.exception.AuthenticationFailedException;
import com.example.security.exception.JwtExpiredTokenException;
import com.example.security.exception.JwtInvalidTokenException;
import com.example.security.exception.NewPasswordRequiredException;
import com.example.security.exception.UserNotCreatedException;
import com.example.security.model.CognitoAuthenticationResultHolder;

@Component
public class ExampleAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private ObjectMapper mapper;

	@Autowired
	public ExampleAuthenticationFailureHandler(final ObjectMapper mapper,
			final CognitoAuthenticationResultHolder cognitoAuthenticationResultHolder) {
		this.mapper = mapper;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if (exception instanceof BadCredentialsException) {
			mapper.writeValue(response.getWriter(), ErrorResponse.of("Invalid username or password",
					ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof AuthMethodNotSupportedException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof NewPasswordRequiredException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.NEW_PASSWORD_REQUIRED, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof AuthenticationFailedException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof UserNotCreatedException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.USER_CREATION_FAILED, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof JwtExpiredTokenException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
		} else if (exception instanceof JwtInvalidTokenException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(exception.getMessage(), ErrorCode.JWT_TOKEN_INVLID, HttpStatus.UNAUTHORIZED));
		}

		mapper.writeValue(response.getWriter(),
				ErrorResponse.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
	}
}
