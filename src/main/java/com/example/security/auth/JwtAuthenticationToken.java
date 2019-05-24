package com.example.security.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.example.security.model.UserContext;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -8325940479892138927L;

	private String token;
	private UserContext userContext;

	public JwtAuthenticationToken(String unsafeToken) {
		super(null);
		this.token = unsafeToken;
		this.setAuthenticated(false);
	}

	public JwtAuthenticationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.eraseCredentials();
		this.userContext = userContext;
		super.setAuthenticated(true);
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		if (authenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}
		super.setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return this.userContext;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		this.token = null;
	}
}