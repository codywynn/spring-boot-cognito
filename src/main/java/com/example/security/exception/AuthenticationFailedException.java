package com.example.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthenticationFailedException extends AuthenticationServiceException {

	private static final long serialVersionUID = -6530555876426535732L;

	public AuthenticationFailedException(String msg) {
		super(msg);
	}
}
