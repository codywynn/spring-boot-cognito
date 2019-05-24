package com.example.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class NewPasswordRequiredException extends AuthenticationServiceException {

	private static final long serialVersionUID = -2562913686718171943L;

	public NewPasswordRequiredException(String msg) {
		super(msg);
	}

}
