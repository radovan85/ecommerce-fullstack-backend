package com.radovan.spring.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.AdminMessageDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.AdminMessageEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.AdminMessageRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.service.AdminMessageService;
import com.radovan.spring.service.UserService;

@Service
@Transactional
public class AdminMessageServiceImpl implements AdminMessageService {

	@Autowired
	private AdminMessageRepository messageRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserService userService;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	@Override
	public AdminMessageDto addMessage(AdminMessageDto message) {
		// TODO Auto-generated method stub
		AdminMessageDto returnValue = null;
		UserDto authUser = userService.getCurrentUser();
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(authUser.getId()));
		if (customerOpt.isPresent()) {
			message.setCustomerId(customerOpt.get().getCustomerId());
			LocalDateTime currentTime = LocalDateTime.now();
			message.setCreatedAtStr(currentTime.format(formatter));
			AdminMessageEntity messageEntity = tempConverter.messageDtoToEntity(message);
			AdminMessageEntity storedMessage = messageRepository.save(messageEntity);
			returnValue = tempConverter.messageEntityToDto(storedMessage);
		} else {
			Error error = new Error("Customer not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public AdminMessageDto getMessageById(Integer messageId) {
		// TODO Auto-generated method stub
		AdminMessageDto returnValue = null;
		Optional<AdminMessageEntity> messageOpt = messageRepository.findById(messageId);
		if (messageOpt.isPresent()) {
			returnValue = tempConverter.messageEntityToDto(messageOpt.get());
		} else {
			Error error = new Error("Message not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public void deleteMessage(Integer messageId) {
		// TODO Auto-generated method stub
		Optional<AdminMessageEntity> messageOpt = messageRepository.findById(messageId);
		if (messageOpt.isPresent()) {
			messageRepository.deleteById(messageId);
			messageRepository.flush();
		} else {
			Error error = new Error("Message not found!");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public List<AdminMessageDto> listAll() {
		// TODO Auto-generated method stub
		List<AdminMessageDto> returnValue = new ArrayList<AdminMessageDto>();
		Optional<List<AdminMessageEntity>> allMessagesOpt = Optional.ofNullable(messageRepository.findAll());
		if (!allMessagesOpt.isEmpty()) {
			allMessagesOpt.get().forEach((messageEntity) -> {
				AdminMessageDto messageDto = tempConverter.messageEntityToDto(messageEntity);
				returnValue.add(messageDto);
			});
		}
		return returnValue;
	}

}
