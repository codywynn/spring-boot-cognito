package com.example.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class JwtExpiredTokenException extends AuthenticationServiceException {

	private static final long serialVersionUID = 4436655325497884955L;

	public JwtExpiredTokenException(String msg) {
		super(msg);
	}

}
