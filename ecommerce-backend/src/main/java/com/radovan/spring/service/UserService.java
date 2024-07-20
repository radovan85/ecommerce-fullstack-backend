package com.radovan.spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.radovan.spring.dto.UserDto;

public interface UserService {

	List<UserDto> listAll();

	UserDto getCurrentUser();

	UserDto getUserById(Integer userId);

	UserDto getUserByEmail(String email);

	Optional<Authentication> authenticateUser(String username, String password);

	Boolean isAdmin();

	void suspendUser(Integer userId);

	void reactivateUser(Integer userId);

}
