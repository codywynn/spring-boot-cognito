package com.example.security.auth;

public interface TokenExtractor {
	public String extract(String payload);
}
