package com.example.security.filter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.example.model.User;
import com.example.security.auth.JwtAuthenticationToken;
import com.example.security.cognito.AwsCognitoRSAKeyProvider;
import com.example.security.cognito.CognitoConfiguration;
import com.example.security.cognito.CognitoService;
import com.example.security.exception.JwtExpiredTokenException;
import com.example.security.exception.JwtInvalidTokenException;
import com.example.security.model.UserContext;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private static final String ROLE_PREFIX = "ROLE_";

	@Autowired
	private CognitoConfiguration cognitoConfiguration;

	@Autowired
	private CognitoService cognitoService;

	@Override
	public Authentication authenticate(Authentication authentication) {
		try {
			String token = authentication.getCredentials().toString();

			RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(cognitoConfiguration.getJwkUrl());
			Algorithm algorithm = Algorithm.RSA256(keyProvider);
			JWTVerifier jwtVerifier = JWT.require(algorithm).build();

			DecodedJWT decodedJWT = jwtVerifier.verify(token);

			Map<String, Claim> claims = decodedJWT.getClaims();
			String username = claims.get(cognitoConfiguration.getUserNameField()).asString();

			List<String> groups = claims.get(cognitoConfiguration.getGroupsField()).asList(String.class);
			List<GrantedAuthority> grantedAuthorities = convertList(groups,
					group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()));

			User user = cognitoService.getUser(username);
			String name = user.getAttribute("given_name").concat(" ").concat(user.getAttribute("family_name"));
			UserContext context = UserContext.create(username, name, grantedAuthorities);

			return new JwtAuthenticationToken(context, context.getAuthorities());
		} catch (JWTVerificationException exception) {
			throw new JwtExpiredTokenException("Token has expired. ".concat(exception.getMessage()));
		} catch (Exception exception) {
			throw new JwtInvalidTokenException("Invalid Token. ".concat(exception.getMessage()));
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}

	private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
		return from.stream().map(func).collect(Collectors.toList());
	}
}
