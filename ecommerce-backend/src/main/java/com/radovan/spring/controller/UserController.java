package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.service.UserService;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allUsers")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> allUsers = userService.listAll();
		return ResponseEntity.ok().body(allUsers);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/suspendUser/{userId}")
	public ResponseEntity<String> suspendUser(@PathVariable("userId") Integer userId) {
		userService.suspendUser(userId);
		return ResponseEntity.ok().body("User with id " + userId + " is suspended");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/reactivateUser/{userId}")
	public ResponseEntity<String> reactivateUser(@PathVariable("userId") Integer userId) {
		userService.reactivateUser(userId);
		return ResponseEntity.ok().body("User with id " + userId + " is reactivated");
	}
	
	@GetMapping(value="/currentUser")
	public ResponseEntity<UserDto> getCurrentUser(){
		UserDto authUser = userService.getCurrentUser();
		return ResponseEntity.ok(authUser);
	}
}
