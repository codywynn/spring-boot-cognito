package com.example.security.filter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.example.security.cognito.CognitoService;
import com.example.security.exception.NewPasswordRequiredException;
import com.example.security.model.CognitoAuthenticationResultHolder;
import com.example.security.model.UserContext;

import io.jsonwebtoken.lang.Assert;

@Component
public class ExampleAuthenticationProvider implements AuthenticationProvider {

	private final BCryptPasswordEncoder encoder;

	@Autowired
	public ExampleAuthenticationProvider(final BCryptPasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Autowired
	private CognitoService cognitoService;

	@Autowired
	private CognitoAuthenticationResultHolder authenticationHolder;

	@Override
	public Authentication authenticate(Authentication authentication) {
		Assert.notNull(authentication, "No authentication data provided");

		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		Map<String, String> authParams = new HashMap<String, String>();
		authParams.put("USERNAME", username);
		authParams.put("PASSWORD", password);

		AdminInitiateAuthResult authResult = cognitoService.initializeAuthentication(authParams);

		if (authResult == null) {
			throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
		}

		String challengeName = authResult.getChallengeName();

		if (challengeName != null && challengeName.equals(ChallengeNameType.NEW_PASSWORD_REQUIRED.toString())) {
			throw new NewPasswordRequiredException("New Password Challenge");
		}
		authenticationHolder.setAuthResult(authResult.getAuthenticationResult());
		UserContext userContext = UserContext.create(username, null, null);
		return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
