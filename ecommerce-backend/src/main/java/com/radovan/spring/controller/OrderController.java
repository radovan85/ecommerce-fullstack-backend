package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.radovan.spring.dto.BillingAddressDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.BillingAddressService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderAddressService;
import com.radovan.spring.service.OrderItemService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.ShippindAddressService;

@RestController
@RequestMapping(value = "api/order")
public class OrderController {

	@Autowired
	private BillingAddressService billingAddressService;

	@Autowired
	private ShippindAddressService shippingAddressService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderAddressService orderAddressService;

	@Autowired
	private OrderItemService orderItemService;

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/confirmBilling")
	public ResponseEntity<BillingAddressDto> getBillingAddress() {
		BillingAddressDto billingAddress = billingAddressService.retrieveBillingAddress();
		return ResponseEntity.ok().body(billingAddress);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/updateBilling/{billingAddressId}")
	public ResponseEntity<String> updateBillingAddress(@Validated @RequestBody BillingAddressDto address,
			@PathVariable("billingAddressId") Integer billingAddressId, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Address not validated!");
			throw new DataNotValidatedException(error);
		}

		billingAddressService.updateBillingAddress(billingAddressId, address);
		return ResponseEntity.ok().body("Billing address is updated!");
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/confirmShipping")
	public ResponseEntity<ShippingAddressDto> getShippingAddress() {
		ShippingAddressDto address = shippingAddressService.retrieveShippingAddress();
		return ResponseEntity.ok().body(address);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/updateShipping/{shippingAddressId}")
	public ResponseEntity<String> updateShippingAddress(@Validated @RequestBody ShippingAddressDto address,
			@PathVariable("shippingAddressId") Integer shippingAddressId, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Address not validated!");
			throw new DataNotValidatedException(error);
		}

		shippingAddressService.updateShippingAddress(shippingAddressId, address);
		return ResponseEntity.ok().body("Shipping address is updated!");
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PutMapping(value = "/updateCustomer/{customerId}")
	public ResponseEntity<String> updateCustomer(@Validated @RequestBody CustomerDto customer,
			@PathVariable("customerId") Integer customerId, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Customer not validated!");
			throw new DataNotValidatedException(error);
		}

		customerService.updateCustomer(customerId, customer);
		return ResponseEntity.ok().body("Customer is updated!");
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@GetMapping(value = "/currentCustomer")
	public ResponseEntity<CustomerDto> currentCustomer() {
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		return ResponseEntity.ok().body(currentCustomer);
	}

	@PreAuthorize(value = "hasAuthority('ROLE_USER')")
	@PostMapping(value = "/executeOrder")
	public ResponseEntity<String> executeOrder() {
		orderService.addOrder();
		return ResponseEntity.ok("Order completed");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allOrders")
	public ResponseEntity<List<OrderDto>> getAllOrders() {
		List<OrderDto> allOrders = orderService.listAll();
		return ResponseEntity.ok().body(allOrders);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderDetails/{orderId}")
	public ResponseEntity<OrderDto> getOrderDetails(@PathVariable("orderId") Integer orderId) {
		OrderDto order = orderService.getOrderById(orderId);
		return ResponseEntity.ok().body(order);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderAddressDetails/{orderId}")
	public ResponseEntity<OrderAddressDto> getAddressDetails(@PathVariable("orderId") Integer orderId) {
		OrderAddressDto address = orderAddressService.getAddressByOrderId(orderId);
		return ResponseEntity.ok().body(address);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderedItems/{orderId}")
	public ResponseEntity<List<OrderItemDto>> getAllItems(@PathVariable("orderId") Integer orderId) {
		List<OrderItemDto> allItems = orderItemService.listAllByOrderId(orderId);
		return ResponseEntity.ok().body(allItems);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/orderPrice/{orderId}")
	public ResponseEntity<Float> getOrderPrice(@PathVariable("orderId") Integer orderId) {
		Float grandTotal = orderService.calculateGrandTotal(orderId);
		return ResponseEntity.ok().body(grandTotal);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId) {
		orderService.deleteOrder(orderId);
		return ResponseEntity.ok().body("Order with id " + orderId + " is permanently deleted");
	}

}
