package com.example.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
	AUTHENTICATION(11), JWT_TOKEN_EXPIRED(12), JWT_TOKEN_INVLID(13), USER_CREATION_FAILED(21), NEW_PASSWORD_REQUIRED(
			22);

	private int errorCode;

	private ErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@JsonValue
	public int getErrorCode() {
		return errorCode;
	}
}