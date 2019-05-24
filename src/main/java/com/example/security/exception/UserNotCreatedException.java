package com.example.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class UserNotCreatedException extends AuthenticationServiceException {

	private static final long serialVersionUID = 994968871917065399L;

	public UserNotCreatedException(String msg) {
		super(msg);
	}
}
