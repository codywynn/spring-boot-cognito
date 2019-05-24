package com.example.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.security.model.CognitoAuthenticationResultHolder;

@Component
public class ExampleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private ObjectMapper mapper;
	private final CognitoAuthenticationResultHolder cognitoAuthenticationResultHolder;

	@Autowired
	public ExampleAuthenticationSuccessHandler(final ObjectMapper mapper,
			final CognitoAuthenticationResultHolder cognitoAuthenticationResultHolder) {
		this.mapper = mapper;
		this.cognitoAuthenticationResultHolder = cognitoAuthenticationResultHolder;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("token", cognitoAuthenticationResultHolder.getAuthResult().getAccessToken());
		tokenMap.put("expiresIn", cognitoAuthenticationResultHolder.getAuthResult().getExpiresIn().toString());
		tokenMap.put("refreshToken", cognitoAuthenticationResultHolder.getAuthResult().getRefreshToken());

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getWriter(), tokenMap);

		clearAuthenticationAttributes(request);
	}

	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

}
