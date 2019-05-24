package com.example.service.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import com.example.model.Message;
import com.example.service.dao.ResourceDaoImpl;

public class MessageDaoImpl extends ResourceDaoImpl implements MessageDao {

	private static Logger logger = LoggerFactory.getLogger(MessageDao.class);

	@Override
	public List<Message> findAll() throws Exception {
		logger.debug("Find all the messages");
		List<Message> messages = null;
		try {
			messages = dynamoDBMapper.scan(Message.class, new DynamoDBScanExpression());

		} catch (Exception e) {
			logger.debug("Error in finding the Message");
			logger.error(e.getMessage());
			throw new Exception("Error in finding the Message");
		}
		return messages;
	}

	@Override
	public Message findMessageById(String id) throws Exception {
		logger.debug("Find the Message data for {}", id);
		Message message = null;
		try {
			message = dynamoDBMapper.load(Message.class, id);

		} catch (Exception e) {
			logger.debug("Error in finding the Message {}", id);
			logger.error(e.getMessage());
			throw new Exception("Error in finding the Message");
		}
		return message;
	}
}
