package com.example.service;

import com.example.model.Message;
import com.example.service.dao.MessageDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class MessageServiceImpl implements MessageService {

	private static Logger logger = LoggerFactory.getLogger(MessageService.class);

	@Autowired
	MessageDao messageDao;

	@Override
	public Message findMessageById(String id) throws Exception {
		logger.debug("Finding message {}", id);
		Message message = messageDao.findMessageById(id);
		return message;
	}
}
