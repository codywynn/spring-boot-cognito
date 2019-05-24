package com.example.security.model;

import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

@Component
public class CognitoAuthenticationResultHolder {
	private AuthenticationResultType authResult;

	public AuthenticationResultType getAuthResult() {
		return authResult;
	}

	public void setAuthResult(AuthenticationResultType authResult) {
		this.authResult = authResult;
	}

}
