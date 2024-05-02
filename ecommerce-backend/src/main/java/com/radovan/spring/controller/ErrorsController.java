package com.radovan.spring.controller;

import javax.security.auth.login.CredentialNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.FileUploadException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.exceptions.InvalidUserException;
import com.radovan.spring.exceptions.OutOfStockException;
import com.radovan.spring.exceptions.SuspendedUserException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorsController {

	@ExceptionHandler(DataNotValidatedException.class)
	public ResponseEntity<String> handleDataNotValidatedException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InstanceUndefinedException.class)
	public ResponseEntity<String> handleInstanceUndefinedException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(CredentialNotFoundException.class)
	public ResponseEntity<String> handleCredentialsNotFoundException(CredentialNotFoundException exc) {
		return new ResponseEntity<>(exc.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<String> handleInvalidUserException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<String> handleFileUploadException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(ExistingInstanceException.class)
	public ResponseEntity<String> handleExistingInstanceException(Error error) {
		return new ResponseEntity<>(error.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<String> handleMultipartException(HttpServletRequest request, Exception e) {
		return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InvalidCartException.class)
	public ResponseEntity<String> handleInvalidCartException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(OutOfStockException.class)
	public ResponseEntity<String> handleOutOfStockException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(SuspendedUserException.class)
	public ResponseEntity<String> handleSuspendedUserException(Error error) {
		return new ResponseEntity<String>(error.getMessage(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
	}
}
