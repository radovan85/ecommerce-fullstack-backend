package com.radovan.spring.interceptors;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.exceptions.SuspendedUserException;
import com.radovan.spring.service.UserService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		Optional<UserDto> authUserOpt = Optional.ofNullable(userService.getCurrentUser());
		if (authUserOpt.isPresent()) {
			UserDto authUser = authUserOpt.get();
			if (authUser.getEnabled() == 0) {
				Error error = new Error("Account suspended");
				throw new SuspendedUserException(error);
			}
		}

		return true;
	}

}
