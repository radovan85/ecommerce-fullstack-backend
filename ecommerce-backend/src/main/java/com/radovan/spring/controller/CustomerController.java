package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CustomerService;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartItemService cartItemService;

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allCustomers")
	public ResponseEntity<List<CustomerDto>> getAllCustomers() {
		List<CustomerDto> allCustomers = customerService.listAll();
		return ResponseEntity.ok().body(allCustomers);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/customerDetails/{customerId}")
	public ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable("customerId") Integer customerId) {
		CustomerDto customer = customerService.getCustomerById(customerId);
		return ResponseEntity.ok().body(customer);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCustomer/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("customerId") Integer customerId) {
		CustomerDto customer = customerService.getCustomerById(customerId);
		cartItemService.removeAllByCartId(customer.getCartId());
		customerService.deleteCustomer(customerId);
		return ResponseEntity.ok().body("Customer with id " + customerId + " is permanently deleted");
	}
}
