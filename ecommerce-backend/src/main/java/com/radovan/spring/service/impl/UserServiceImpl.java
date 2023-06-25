package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.InvalidUserException;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TempConverter tempConverter;

	@Override
	public void deleteUser(Integer id) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOpt = userRepository.findById(id);
		if (userOpt.isPresent()) {
			userRepository.deleteById(id);
			userRepository.flush();
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}

	}

	@Override
	public UserDto getUserById(Integer id) {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		Optional<UserEntity> userOpt = userRepository.findById(id);
		if (userOpt.isPresent()) {
			returnValue = tempConverter.userEntityToDto(userOpt.get());
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}
		return returnValue;
	}

	@Override
	public List<UserDto> listAll() {
		// TODO Auto-generated method stub
		List<UserDto> returnValue = new ArrayList<UserDto>();
		Optional<List<UserEntity>> allUsers = Optional.ofNullable(userRepository.findAll());
		if (!allUsers.isEmpty()) {
			allUsers.get().forEach((user) -> {
				UserDto userDto = tempConverter.userEntityToDto(user);
				returnValue.add(userDto);
			});
		}
		return returnValue;
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		// TODO Auto-generated method stub
		UserEntity returnValue = null;
		Optional<UserEntity> userOpt = Optional.ofNullable(userRepository.findByEmail(email));
		if (userOpt.isPresent()) {
			returnValue = userOpt.get();
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}
		return returnValue;
	}

	@Override
	public UserDto getCurrentUser() {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				String currentUserName = authentication.getName();
				UserEntity authUser = userRepository.findByEmail(currentUserName);
				returnValue = tempConverter.userEntityToDto(authUser);
			}
		} catch (Exception exc) {
			Error error = new Error("Invalid user!");
			throw new InvalidUserException(error);
		}
		return returnValue;
	}

	@Override
	public void suspendUser(Integer userId) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			UserEntity userEntity = userOpt.get();
			userEntity.setEnabled((byte) 0);
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}
	}

	@Override
	public void reactivateUser(Integer userId) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			UserEntity userEntity = userOpt.get();
			userEntity.setEnabled((byte) 1);
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}
	}

	@Override
	public UserDto updateUser(Integer userId, UserDto user) {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		Optional<UserEntity> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			user.setId(userId);
			UserEntity userEntity = tempConverter.userDtoToEntity(user);
			UserEntity updatedUser = userRepository.saveAndFlush(userEntity);
			returnValue = tempConverter.userEntityToDto(updatedUser);
		} else {
			Error error = new Error("User not found!");
			throw new InvalidUserException(error);
		}
		return returnValue;
	}

}
