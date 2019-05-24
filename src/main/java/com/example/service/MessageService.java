package com.example.service;

import com.example.model.Message;

public interface MessageService {

	public Message findMessageById(String id) throws Exception;
}
