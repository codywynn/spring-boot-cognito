package com.example.service.dao;

public interface ResourceDao {
	public <T> Object create(T object) throws Exception;
}
