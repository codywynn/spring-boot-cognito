package com.example.security.cognito;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.example.model.User;

public interface CognitoService {

	public AdminInitiateAuthResult initializeAuthentication(Map<String, String> authParams);

	public Map<String, String> getToken(String refreshToken);

	public ListUsersResult getUsers();

	public List<UserType> getUsersFromGroup(String groupName);

	public User getUser(String username);

	public List<String> getUserGroups(String username);

	public User createUser(User user);

	public User updateUser(User user);

	public boolean enableUser(User user);

	public boolean disableUser(User user);

	public boolean addUserToGroup(String username, String groupName);

	public boolean forgotPassword(String username);

	public boolean confirmForgotPassword(String username, String password, String confirmationCode);

	public boolean resetPassword(String username, String previousPassword, String proposedPassword);

}
