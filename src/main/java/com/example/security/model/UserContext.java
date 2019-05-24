package com.example.security.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

public class UserContext {
	private final String username;
	private final String name;
	private final List<GrantedAuthority> authorities;

	public UserContext(String username, String name, List<GrantedAuthority> authorities) {
		this.username = username;
		this.name = name;
		this.authorities = authorities;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public static UserContext create(String username, String name, List<GrantedAuthority> authorities) {
		if (StringUtils.isBlank(username))
			throw new IllegalArgumentException("Username is blank: " + username);
		return new UserContext(username, name, authorities);
	}
}
