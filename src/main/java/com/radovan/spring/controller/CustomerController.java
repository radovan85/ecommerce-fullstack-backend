package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CustomerService;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/getCurrentCustomer")
	public ResponseEntity<CustomerDto> getCurrentCustomer() {
		CustomerDto customer = customerService.getCurrentCustomer();
		return new ResponseEntity<>(customer, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/updateCustomer")
	public ResponseEntity<String> updateCustomer(@Validated @RequestBody CustomerDto customer, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The customer has not been validated!"));
		}

		customerService.updtadeCustomer(customer);
		return new ResponseEntity<>("The customer has been updated!", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allCustomers")
	public ResponseEntity<List<CustomerDto>> getAllCustomers() {
		List<CustomerDto> allCustomers = customerService.listAll();
		return new ResponseEntity<>(allCustomers, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/customerDetails/{customerId}")
	public ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable("customerId") Integer customerId) {
		CustomerDto customer = customerService.getCustomerById(customerId);
		return new ResponseEntity<>(customer, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCustomer/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("customerId") Integer customerId) {
		customerService.removeCustomer(customerId);
		return new ResponseEntity<String>("The customer with id " + customerId + " has been permanently deleted!",
				HttpStatus.OK);
	}
}
