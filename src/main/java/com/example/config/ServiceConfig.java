package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.service.MessageService;
import com.example.service.MessageServiceImpl;

@Configuration
public class ServiceConfig {

	@Bean
	public MessageService messageService() {
		
		return new MessageServiceImpl();
	}
}
