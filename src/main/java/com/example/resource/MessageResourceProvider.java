package com.example.resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.model.UserContext;
import com.example.util.DateUtil;

@RestController
public class MessageResourceProvider {

	private Logger logger = LoggerFactory.getLogger(MessageResourceProvider.class);

	/**
	 * The method retrieves the message from id
	 * 
	 * @param id
	 * @return Message
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/message/{id}")
	public ResponseEntity<?> getMessage(@PathVariable String id) {
		logger.info("Request to /api/message");
		return new ResponseEntity<String>("Hello World!", HttpStatus.OK);
	}
}
