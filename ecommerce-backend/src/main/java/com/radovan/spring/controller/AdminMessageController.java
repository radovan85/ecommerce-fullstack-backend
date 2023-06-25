package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.AdminMessageDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.AdminMessageService;

@RestController
@RequestMapping(value = "/api/messages")
public class AdminMessageController {

	@Autowired
	private AdminMessageService messageService;

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PostMapping(value = "/sendMessage")
	public ResponseEntity<String> sendMessage(@Validated @RequestBody AdminMessageDto message, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Message not validated!");
			throw new DataNotValidatedException(error);
		}

		messageService.addMessage(message);
		return ResponseEntity.ok().body("Message sent!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allMessages")
	public ResponseEntity<List<AdminMessageDto>> getAllMessages() {
		List<AdminMessageDto> allMessages = messageService.listAll();
		return ResponseEntity.ok().body(allMessages);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/messageDetails/{messageId}")
	public ResponseEntity<AdminMessageDto> getMessageDetails(@PathVariable("messageId") Integer messageId) {
		AdminMessageDto message = messageService.getMessageById(messageId);
		return ResponseEntity.ok().body(message);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteMessage/{messageId}")
	public ResponseEntity<String> deleteMessage(@PathVariable("messageId") Integer messageId) {
		messageService.deleteMessage(messageId);
		return ResponseEntity.ok().body("Message with id " + messageId + " is permanently deleted!");
	}
}
