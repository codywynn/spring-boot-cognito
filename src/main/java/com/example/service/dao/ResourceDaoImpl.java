package com.example.service.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class ResourceDaoImpl implements ResourceDao {

	private static Logger logger = LoggerFactory.getLogger(ResourceDao.class);

	@Autowired
	protected DynamoDBMapper dynamoDBMapper;

	public <T> Object create(T object) throws Exception {
		logger.debug("Creating the object");
		try {
			dynamoDBMapper.save(object);
		} catch (Exception e) {
			logger.debug("Error in creating the obejct");
			logger.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
		return object;
	}
}
