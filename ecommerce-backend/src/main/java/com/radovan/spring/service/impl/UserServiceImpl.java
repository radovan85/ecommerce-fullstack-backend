package com.radovan.spring.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.InvalidUserException;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> listAll() {
		return userRepository.findAll().stream().map(tempConverter::userEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.isAuthenticated()) {
			String currentUsername = authentication.getName();
			UserEntity user = userRepository.findByEmail(currentUsername)
					.orElseThrow(() -> new InvalidUserException(new Error("Invalid user!")));
			return tempConverter.userEntityToDto(user);
		} else {
			throw new InvalidUserException(new Error("Invalid user!"));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(Integer userId) {
		return userRepository.findById(userId).map(tempConverter::userEntityToDto)
				.orElseThrow(() -> new InvalidUserException(new Error("Invalid user!")));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(tempConverter::userEntityToDto)
				.orElseThrow(() -> new InvalidUserException(new Error("Invalid user!")));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Authentication> authenticateUser(String username, String password) {
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		Optional<UserEntity> userOptional = userRepository.findByEmail(username);
		return userOptional.flatMap(user -> {
			try {
				Authentication auth = authenticationManager.authenticate(authReq);
				return Optional.of(auth);
			} catch (AuthenticationException e) {
				// Handle authentication failure
				return Optional.empty();
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isAdmin() {
		// TODO Auto-generated method stub
		Boolean returnValue = false;
		UserDto authUser = getCurrentUser();
		RoleEntity roleEntity = roleRepository.findByRole("ADMIN").orElse(null);
		if (roleEntity != null) {
			if (authUser.getRolesIds().contains(roleEntity.getId())) {
				returnValue = true;
			}
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void suspendUser(Integer userId) {
		// TODO Auto-generated method stub
		UserDto user = getUserById(userId);
		user.setEnabled((short) 0);
		userRepository.saveAndFlush(tempConverter.userDtoToEntity(user));

	}

	@Override
	@Transactional
	public void reactivateUser(Integer userId) {
		// TODO Auto-generated method stub
		UserDto user = getUserById(userId);
		user.setEnabled((short) 1);
		userRepository.saveAndFlush(tempConverter.userDtoToEntity(user));
	}

}
