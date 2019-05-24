package com.example.security.cognito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

@Component
public class CognitoConfiguration {

	private Logger logger = LoggerFactory.getLogger(CognitoConfiguration.class);

	private static final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
	private static final String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";

	@Value("${aws.cognito.userPoolId}")
	private String userPoolId;

	@Value("${aws.cognito.identityPoolId}")
	private String identityPoolId;

	@Value("${aws.cognito.region}")
	private String region;

	@Value("${aws.cognito.userNameField}")
	private String userNameField;

	@Value("${aws.cognito.groupsField}")
	private String groupsField;

	@Value("${aws.cognito.connectionTimeout}")
	private int connectionTimeout;

	@Value("${aws.cognito.readTimeout}")
	private int readTimeout;

	@Value("${aws.cognito.clientId}")
	private String clientId;

	private String httpHeader = "Authorization";
	private String jwkUrl;

	@Bean
	public AWSCognitoIdentityProvider cognitoIdentityProvider() throws Exception {
		logger.debug("Configuring Cognito");
		AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider()).build();
		logger.debug("Cognito initialized successfully");
		return cognitoIdentityProvider;
	}

	@Bean
	public CognitoService cognitoService() {
		return new CognitoServiceImpl();
	}

	public String getJwkUrl() {
		if (jwkUrl == null || jwkUrl.isEmpty()) {
			return String.format(COGNITO_IDENTITY_POOL_URL + JSON_WEB_TOKEN_SET_URL_SUFFIX, region, userPoolId);
		}
		return jwkUrl;
	}

	public String getCognitoIdentityPoolUrl() {
		return String.format(COGNITO_IDENTITY_POOL_URL, region, userPoolId);
	}

	public void setJwkUrl(String jwkUrl) {
		this.jwkUrl = jwkUrl;
	}

	public String getUserPoolId() {
		return userPoolId;
	}

	public void setUserPoolId(String userPoolId) {
		this.userPoolId = userPoolId;
	}

	public String getIdentityPoolId() {
		return identityPoolId;
	}

	public CognitoConfiguration setIdentityPoolId(String identityPoolId) {
		this.identityPoolId = identityPoolId;
		return this;
	}

	public String getHttpHeader() {
		return httpHeader;
	}

	public void setHttpHeader(String httpHeader) {
		this.httpHeader = httpHeader;
	}

	public String getUserNameField() {
		return userNameField;
	}

	public void setUserNameField(String userNameField) {
		this.userNameField = userNameField;
	}

	public String getGroupsField() {
		return groupsField;
	}

	public void setGroupsField(String groupsField) {
		this.groupsField = groupsField;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
