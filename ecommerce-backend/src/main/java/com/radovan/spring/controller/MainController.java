package com.radovan.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RegistrationForm;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.SuspendedUserException;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.UserService;
import com.radovan.spring.utils.JwtUtil;

@RestController
@RequestMapping(value = "/api")
public class MainController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/login")
	public ResponseEntity<UserDto> createAuthenticationToken(
			@RequestBody com.radovan.spring.dto.AuthenticationRequest authenticationRequest) throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Password incorrect", e);
		}

		final UserEntity userDetails = userService.getUserByEmail(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		UserDto authUser = tempConverter.userEntityToDto(userDetails);
		authUser.setAuthToken(jwt);

		return ResponseEntity.ok().body(authUser);

	}
	
	@GetMapping(value="/checkForSuspension")
	public ResponseEntity<String> suspensionChecking(){
		UserDto authUser = userService.getCurrentUser();
		if(authUser.getEnabled() == 0) {
			Error error = new Error("Account suspended!");
			throw new SuspendedUserException(error);
		}
		
		return ResponseEntity.ok().body("No suspension!");
	}

	@PostMapping(value = "/register")
	public ResponseEntity<String> registerUser(@Validated @RequestBody RegistrationForm form, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Data is not validated!");
			throw new DataNotValidatedException(error);
		}

		customerService.storeCustomer(form);
		return ResponseEntity.ok().body("Registration completed!");
	}

	@PostMapping(value = "/loggedout")
	public ResponseEntity<String> logout() {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(null);
		SecurityContextHolder.clearContext();
		return ResponseEntity.ok().body("Logged out successfully!");
	}
}
