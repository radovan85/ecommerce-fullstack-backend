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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderAddressService;
import com.radovan.spring.service.OrderItemService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.ShippingAddressService;

@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

	@Autowired
	private ShippingAddressService shippingAddressService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderAddressService orderAddressService;

	@Autowired
	private OrderItemService orderItemService;

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/provideMyAddress")
	public ResponseEntity<ShippingAddressDto> provideMyAddress() {
		CustomerDto customer = customerService.getCurrentCustomer();
		ShippingAddressDto address = shippingAddressService.getAddressById(customer.getShippingAddressId());
		return new ResponseEntity<>(address, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/confirmShippingAddress")
	public ResponseEntity<String> confirmShipping(@Validated @RequestBody ShippingAddressDto address, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The address has not been validated!"));
		}
		CustomerDto customer = customerService.getCurrentCustomer();
		shippingAddressService.updateAddress(address, customer.getShippingAddressId());
		return new ResponseEntity<String>("The address has been updated!", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PostMapping(value = "/placeOrder")
	public ResponseEntity<String> placeOrder() {
		orderService.addOrder();
		return new ResponseEntity<String>("Your order has been submitted without any problems.", HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allOrders")
	public ResponseEntity<List<OrderDto>> getAllOrders() {
		List<OrderDto> allOrders = orderService.listAll();
		return new ResponseEntity<>(allOrders, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderDetails/{orderId}")
	public ResponseEntity<OrderDto> orderDetails(@PathVariable("orderId") Integer orderId) {
		OrderDto order = orderService.getOrderById(orderId);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allAddresses")
	public ResponseEntity<List<OrderAddressDto>> getAllAddresses() {
		List<OrderAddressDto> allAddresses = orderAddressService.listAll();
		return new ResponseEntity<>(allAddresses, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allItems/{orderId}")
	public ResponseEntity<List<OrderItemDto>> getAllItems(@PathVariable("orderId") Integer orderId) {
		List<OrderItemDto> allItems = orderItemService.listAllByOrderId(orderId);
		return new ResponseEntity<>(allItems, HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId) {
		orderService.deleteOrder(orderId);
		return new ResponseEntity<String>("The order with id " + orderId + " has been permanently deleted!",
				HttpStatus.OK);
	}
}
