package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import com.example.model.Message;
import com.example.service.dao.MessageDao;
import com.example.service.dao.MessageDaoImpl;
import com.example.service.dao.ResourceDao;
import com.example.service.dao.ResourceDaoImpl;

@Configuration
public class DynamoDBConfig {

	private Logger logger = LoggerFactory.getLogger(DynamoDBConfig.class);

	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider()).build();
		return dynamoDB;
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		AmazonDynamoDB dynamoDB = amazonDynamoDB();
		DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);

		// Create Message table
		logger.debug("Create Message table, if it does not exist");
		if (!doesTableExist(dynamoDB, "Message")) {
			CreateTableRequest request = dynamoDBMapper.generateCreateTableRequest(Message.class);
			request.setProvisionedThroughput(
					new ProvisionedThroughput().withReadCapacityUnits((long) 5).withWriteCapacityUnits((long) 5));
			try {
				logger.debug("Message table does not exist");
				dynamoDB.createTable(request);
			} catch (final ResourceNotFoundException e) {
				logger.debug("Not able to create the table {}", request.getTableName());
				logger.error(e.getMessage());
			}
			logger.debug("Message table has been created");
		}
		logger.debug("DynamoDB initialized successfully");

		return dynamoDBMapper;
	}

	public static boolean doesTableExist(final AmazonDynamoDB dynamoDB, final String tableName) {
		try {
			dynamoDB.describeTable(tableName);
			return true;
		} catch (final ResourceNotFoundException e) {
			return false;
		} catch (final AmazonClientException e) {
			return false;
		}

	}

	@Bean
	public ResourceDao resourceDao() {
		return new ResourceDaoImpl();
	}

	@Bean
	public MessageDao messagedao() {
		return new MessageDaoImpl();
	}
}
