package com.example.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;

public class User {

	private Logger logger = LoggerFactory.getLogger(User.class);

	private UserType user;
	private List<String> groups;

	public UserType getUser() {
		return user;
	}

	public void setUser(UserType user) {
		this.user = user;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * The method reads the value of the user attribute
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getAttribute(String attributeName) {
		logger.debug("Get the value of the attribute : {}", attributeName);
		logger.debug("Iterate thro the available attributes");
		for (AttributeType attribute : user.getAttributes()) {
			logger.debug("Attribute name : {}", attribute.getName());
			if (attribute.getName().equals(attributeName)) {
				return attribute.getValue();
			}
		}
		return null;
	}

}
