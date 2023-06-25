package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;

public interface UserService {

	void deleteUser(Integer id);

	UserDto getUserById(Integer id);

	List<UserDto> listAll();

	UserEntity getUserByEmail(String email);

	UserDto getCurrentUser();

	void suspendUser(Integer userId);

	void reactivateUser(Integer userId);
	
	UserDto updateUser(Integer userId,UserDto user);
}
