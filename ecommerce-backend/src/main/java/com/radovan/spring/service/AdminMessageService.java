package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.AdminMessageDto;

public interface AdminMessageService {

	AdminMessageDto addMessage(AdminMessageDto message);
	
	AdminMessageDto getMessageById(Integer messageId);
	
	void deleteMessage(Integer messageId);
	
	List<AdminMessageDto> listAll();
}
