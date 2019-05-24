package com.example.security.cognito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.PasswordResetRequiredException;
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.example.model.User;
import com.example.security.exception.AuthenticationFailedException;
import com.example.security.exception.UserNotCreatedException;
import com.example.util.PasswordUtil;

public class CognitoServiceImpl implements CognitoService {

	private static final Logger logger = Logger.getLogger(CognitoServiceImpl.class.getName());

	@Autowired
	private CognitoConfiguration cognitoConfiguration;

	@Autowired
	private AWSCognitoIdentityProvider cognitoIdentityProvider;

	@Override
	public AdminInitiateAuthResult initializeAuthentication(Map<String, String> authParams) {
		AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
				.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).withClientId(cognitoConfiguration.getClientId())
				.withUserPoolId(cognitoConfiguration.getUserPoolId()).withAuthParameters(authParams);
		AdminInitiateAuthResult authResult = null;
		try {
			authResult = cognitoIdentityProvider.adminInitiateAuth(authRequest);
		} catch (NotAuthorizedException notAuthorizedException) {
			String message = "Error in User Authentication. ".concat(notAuthorizedException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new AuthenticationFailedException(message);
		} catch (UserNotFoundException userNotFoundException) {
			String message = "Error in User Authentication. ".concat(userNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new AuthenticationFailedException(message);
		} catch (PasswordResetRequiredException passwordResetRequiredException) {
			String message = "Error in User Authentication. ".concat(passwordResetRequiredException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new AuthenticationFailedException(message);
		}
		return authResult;
	}

	@Override
	public ListUsersResult getUsers() {
		ListUsersRequest listUsersRequest = new ListUsersRequest();
		listUsersRequest.setUserPoolId(cognitoConfiguration.getUserPoolId());
		return cognitoIdentityProvider.listUsers(listUsersRequest);
	}

	@Override
	public List<UserType> getUsersFromGroup(String groupName) {
		ListUsersInGroupRequest listUsersInGroupRequest = new ListUsersInGroupRequest();
		listUsersInGroupRequest.setUserPoolId(cognitoConfiguration.getUserPoolId());
		listUsersInGroupRequest.setGroupName(groupName);
		ListUsersInGroupResult listUsersInGroupResult = cognitoIdentityProvider
				.listUsersInGroup(listUsersInGroupRequest);
		if (listUsersInGroupResult != null) {
			return listUsersInGroupResult.getUsers();
		} else {
			return null;
		}
	}

	@Override
	public User getUser(String username) {
		User user = null;
		try {
			AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);
			AdminGetUserResult userResult = cognitoIdentityProvider.adminGetUser(getUserRequest);
			if (userResult != null) {
				user = new User();
				UserType userType = new UserType();
				userType.setUsername(userResult.getUsername());
				userType.setAttributes(userResult.getUserAttributes());
				userType.setUserStatus(userResult.getUserStatus());
				userType.setUserCreateDate(userResult.getUserCreateDate());
				userType.setEnabled(userResult.getEnabled());
				userType.setUserLastModifiedDate(userResult.getUserLastModifiedDate());
				user.setUser(userType);
				user.setGroups(getUserGroups(userResult.getUsername()));
			}

		} catch (UserNotFoundException exception) {
			String message = "Error in finding the User. ".concat(exception.getErrorMessage());
			logger.log(Level.INFO, message);
		}
		return user;
	}

	@Override
	public List<String> getUserGroups(String username) {
		List<String> groups = null;
		try {
			AdminListGroupsForUserRequest listGroupsForUserRequest = new AdminListGroupsForUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);

			AdminListGroupsForUserResult listGroupsForUserResult = cognitoIdentityProvider
					.adminListGroupsForUser(listGroupsForUserRequest);
			if (listGroupsForUserResult != null && listGroupsForUserResult.getGroups() != null) {
				groups = new ArrayList<String>();
				for (GroupType groupType : listGroupsForUserResult.getGroups()) {
					groups.add(groupType.getGroupName());
				}
			}
		} catch (UserNotFoundException exception) {
			String message = "Error in finding the User. ".concat(exception.getErrorMessage());
			logger.log(Level.INFO, message);
		}
		return groups;

	}

	@Override
	public User createUser(User user) {
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
		createUserRequest.setUserPoolId(cognitoConfiguration.getUserPoolId());
		createUserRequest.setUsername(user.getUser().getUsername());
		createUserRequest.setUserAttributes(user.getUser().getAttributes());
		// Set the email verified flag
		createUserRequest.getUserAttributes().add(new AttributeType().withName("email_verified").withValue("true"));

		String temporaryPassword = PasswordUtil.generateTemporaryPassword();
		createUserRequest.withTemporaryPassword(temporaryPassword);
		createUserRequest.withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);

		try {
			AdminCreateUserResult createUserResult = cognitoIdentityProvider.adminCreateUser(createUserRequest);
			if (createUserResult != null) {
				for (String groupName : user.getGroups())
					addUserToGroup(createUserResult.getUser().getUsername(), groupName);

				UserType cognitoUser = createUserResult.getUser();
				user.setUser(cognitoUser);
				return user;
			} else {
				throw new AuthenticationFailedException("Error in creating the User. ");
			}
		} catch (UsernameExistsException usernameExistsException) {
			String message = "Error in creating the User. ".concat(usernameExistsException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new UsernameExistsException(usernameExistsException.getErrorMessage());
		} catch (InvalidPasswordException invalidPasswordException) {
			String message = "Error in creating the User. ".concat(invalidPasswordException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new UserNotCreatedException(message);
		} catch (Exception exception) {
			String message = "Error in creating the User. ".concat(exception.getMessage());
			logger.log(Level.INFO, message);
			throw new UserNotCreatedException(message);
		}
	}

	@Override
	public User updateUser(User user) {
		try {
			if (user != null) {
				String username = user.getUser().getUsername();
				// Enable or Disable User based on Active/Inactive in the UI
				if (user.getUser().getEnabled()) {
					enableUser(user);
				} else {
					disableUser(user);
				}

				// Update User Groups
				// Read the existing user groups and delete it
				for (GroupType group : listUserGroups(user)) {
					removeUserFromGroup(username, group.getGroupName());
				}

				// Add the user to the new group
				for (String groupName : user.getGroups()) {
					addUserToGroup(username, groupName);
				}

				AdminUpdateUserAttributesRequest updateUserAttributeRequest = new AdminUpdateUserAttributesRequest()
						.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);
				updateUserAttributeRequest.setUserAttributes(user.getUser().getAttributes());
				AdminUpdateUserAttributesResult updateUserAttributesResult = cognitoIdentityProvider
						.adminUpdateUserAttributes(updateUserAttributeRequest);
				if (updateUserAttributesResult != null) {
					return user;
				} else {
					throw new UserNotCreatedException("Error in updating the User.");
				}
			} else {
				throw new UserNotCreatedException("Error in updating the User.");
			}
		} catch (UsernameExistsException usernameExistsException) {
			String message = "Error in updating the User. ".concat(usernameExistsException.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new UsernameExistsException(usernameExistsException.getErrorMessage());
		} catch (Exception exception) {
			String message = "Error in updating the User. ".concat(exception.getMessage());
			logger.log(Level.INFO, message);
			throw new UserNotCreatedException(message);
		}
	}

	@Override
	public boolean resetPassword(String username, String previousPassword, String proposedPassword) {
		Map<String, String> authParams = new HashMap<String, String>();
		authParams.put("USERNAME", username);
		authParams.put("PASSWORD", previousPassword);

		AdminInitiateAuthResult authResult = initializeAuthentication(authParams);
		if (authResult == null) {
			throw new BadCredentialsException("Username or Password not valid.");
		}
		String challengeName = authResult.getChallengeName();

		if (challengeName != null && challengeName.equals(ChallengeNameType.NEW_PASSWORD_REQUIRED.toString())) {
			authParams.put("NEW_PASSWORD", proposedPassword);
			AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
			request.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED).withChallengeResponses(authParams)
					.withClientId(cognitoConfiguration.getClientId())
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withSession(authResult.getSession());

			AdminRespondToAuthChallengeResult challengeResult = cognitoIdentityProvider
					.adminRespondToAuthChallenge(request);

			if (challengeResult != null) {
				logger.log(Level.INFO, "User password has been updated.");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean forgotPassword(String username) throws UsernameExistsException {
		ForgotPasswordRequest forgotPasswordRequest = null;
		try {
			AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);
			AdminGetUserResult userResult = cognitoIdentityProvider.adminGetUser(getUserRequest);
			if (userResult.getUsername() != null) {

				forgotPasswordRequest = new ForgotPasswordRequest().withClientId(cognitoConfiguration.getClientId())
						.withUsername(userResult.getUsername());
			}
			ForgotPasswordResult forgotPasswordResult = cognitoIdentityProvider.forgotPassword(forgotPasswordRequest);
			if (forgotPasswordResult != null) {
				return true;
			}
		} catch (UsernameExistsException exception) {
			String message = "Error in creating the User. ".concat(exception.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new UsernameExistsException(exception.getErrorMessage());
		}
		return false;
	}

	@Override
	public boolean confirmForgotPassword(String username, String password, String confirmationCode)
			throws UsernameExistsException {
		ConfirmForgotPasswordRequest confirmForgotPasswordRequest = null;
		try {
			AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);
			AdminGetUserResult userResult = cognitoIdentityProvider.adminGetUser(getUserRequest);
			if (userResult.getUsername() != null) {

				confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest()
						.withClientId(cognitoConfiguration.getClientId()).withUsername(username).withPassword(password)
						.withConfirmationCode(confirmationCode);
			}
			ConfirmForgotPasswordResult confirmForgotPasswordResult = cognitoIdentityProvider
					.confirmForgotPassword(confirmForgotPasswordRequest);
			if (confirmForgotPasswordResult != null) {
				return true;
			}
		} catch (UsernameExistsException exception) {
			String message = "Error in creating the User. ".concat(exception.getErrorMessage());
			logger.log(Level.INFO, message);
			throw new UsernameExistsException(exception.getErrorMessage());
		}
		return false;
	}

	@Override
	public boolean addUserToGroup(String username, String groupName) {
		AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest()
				.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username).withGroupName(groupName);
		try {
			cognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
		} catch (ResourceNotFoundException resourceNotFoundException) {
			String message = "Error in assiging group to the User. "
					.concat(resourceNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
			return false;
		} catch (UserNotFoundException userNotFoundException) {
			String message = "Error in assiging group to the User. ".concat(userNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
			return false;
		}
		return true;
	}

	public List<GroupType> listUserGroups(User user) {
		AdminListGroupsForUserRequest listGroupsForUserRequest = new AdminListGroupsForUserRequest()
				.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(user.getUser().getUsername());

		try {
			AdminListGroupsForUserResult listGroupsForUserResult = cognitoIdentityProvider
					.adminListGroupsForUser(listGroupsForUserRequest);
			if (listGroupsForUserResult != null)
				return listGroupsForUserResult.getGroups();
			else
				return null;
		} catch (ResourceNotFoundException resourceNotFoundException) {
			String message = "Error in assiging group to the User. "
					.concat(resourceNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
		} catch (UserNotFoundException userNotFoundException) {
			String message = "Error in assiging group to the User. ".concat(userNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
		}
		return null;
	}

	public boolean removeUserFromGroup(String username, String groupName) {
		AdminRemoveUserFromGroupRequest removeUserToGroupRequest = new AdminRemoveUserFromGroupRequest()
				.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username).withGroupName(groupName);
		try {
			AdminRemoveUserFromGroupResult removeUserFromGroupResult = cognitoIdentityProvider
					.adminRemoveUserFromGroup(removeUserToGroupRequest);
			if (removeUserFromGroupResult != null) {
				return true;
			} else {
				return false;
			}
		} catch (ResourceNotFoundException resourceNotFoundException) {
			String message = "Error in assiging group to the User. "
					.concat(resourceNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
		} catch (UserNotFoundException userNotFoundException) {
			String message = "Error in assiging group to the User. ".concat(userNotFoundException.getErrorMessage());
			logger.log(Level.INFO, message);
		}
		return false;
	}

	@Override
	public Map<String, String> getToken(String refreshToken) {
		Map<String, String> authParameters = new HashMap<String, String>();
		authParameters.put("REFRESH_TOKEN", refreshToken);

		InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest().withAuthFlow(AuthFlowType.REFRESH_TOKEN)
				.withClientId(cognitoConfiguration.getClientId()).withAuthParameters(authParameters);

		InitiateAuthResult authResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest);

		Map<String, String> tokenMap = new HashMap<String, String>();
		if (authResult != null) {
			tokenMap.put("token", authResult.getAuthenticationResult().getAccessToken());
			tokenMap.put("expiresIn", authResult.getAuthenticationResult().getExpiresIn().toString());
			tokenMap.put("refreshToken", refreshToken);
		} else {
			String message = "error in generating the refresh token";
			logger.log(Level.INFO, message);
			tokenMap.put("error", message);
		}
		return tokenMap;
	}

	@Override
	public boolean enableUser(User user) {
		if (user != null) {
			AdminEnableUserRequest enableUserRequest = new AdminEnableUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(user.getUser().getUsername());
			AdminEnableUserResult result = cognitoIdentityProvider.adminEnableUser(enableUserRequest);
			if (result != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean disableUser(User user) {
		if (user != null) {
			AdminDisableUserRequest disableUserRequest = new AdminDisableUserRequest()
					.withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(user.getUser().getUsername());
			AdminDisableUserResult result = cognitoIdentityProvider.adminDisableUser(disableUserRequest);
			if (result != null) {
				return true;
			}
		}
		return false;
	}

}
