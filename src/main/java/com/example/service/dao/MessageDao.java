package com.example.service.dao;

import java.util.List;

import com.example.model.Message;

public interface MessageDao extends ResourceDao {

	public List<Message> findAll() throws Exception;

	public Message findMessageById(String id) throws Exception;
}