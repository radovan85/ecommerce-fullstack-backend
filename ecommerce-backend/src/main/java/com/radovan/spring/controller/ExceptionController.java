package com.radovan.spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.ExistingEmailException;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.exceptions.InsufficientStockException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.exceptions.InvalidUserException;
import com.radovan.spring.exceptions.SuspendedUserException;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(DataNotValidatedException.class)
	public ResponseEntity<String> handleDataNotValidatedException() {
		return ResponseEntity.internalServerError().body("Data is not validated!!!");
	}

	@ExceptionHandler(ExistingEmailException.class)
	public ResponseEntity<String> handleExistingEmailException() {
		return ResponseEntity.internalServerError().body("Email exists already!!!");
	}

	@ExceptionHandler(InstanceNotExistException.class)
	public ResponseEntity<String> handleInstanceNotExistException() {
		return ResponseEntity.internalServerError().body("Instance not found!!!");
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<String> handleInsufficientStockException() {
		return ResponseEntity.internalServerError().body("Stock not enough!!!");
	}

	@ExceptionHandler(InvalidCartException.class)
	public ResponseEntity<String> handleInvalidCartException() {
		return ResponseEntity.internalServerError().body("Invalid cart!!!");
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<String> handleInvalidUserException() {
		return ResponseEntity.internalServerError().body("Invalid user!!!");
	}

	@ExceptionHandler(SuspendedUserException.class)
	public ResponseEntity<String> handleSuspendedUserException() {
		return ResponseEntity.internalServerError().body("Account suspended!!!");
	}

}
