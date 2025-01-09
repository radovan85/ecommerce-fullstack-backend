package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;

@RestController
@RequestMapping(value = "/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@GetMapping(value = "/getMyItems")
	public ResponseEntity<List<CartItemDto>> listMyItems() {
		CustomerDto customer = customerService.getCurrentCustomer();
		List<CartItemDto> allItems = cartItemService.listAllByCartId(customer.getCartId());
		return new ResponseEntity<>(allItems, HttpStatus.OK);
	}

	@PostMapping(value = "/addCartItem/{productId}")
	public ResponseEntity<String> addItem(@PathVariable("productId") Integer productId) {
		cartItemService.addCartItem(productId);
		return new ResponseEntity<>("The item has been added to the cart!", HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteItem/{itemId}")
	public ResponseEntity<String> deleteItem(@PathVariable("itemId") Integer itemId) {
		cartItemService.removeCartItem(itemId);
		return new ResponseEntity<>("The item has been removed from the cart!", HttpStatus.OK);
	}

	@DeleteMapping(value = "/clearCart")
	public ResponseEntity<String> clearCart() {
		CustomerDto customer = customerService.getCurrentCustomer();
		cartItemService.removeAllByCartId(customer.getCartId());
		return new ResponseEntity<>("All items have been removed from the cart!", HttpStatus.OK);
	}

	@GetMapping(value = "/getMyCart")
	public ResponseEntity<CartDto> getMyCart() {
		CustomerDto customer = customerService.getCurrentCustomer();
		CartDto cart = cartService.getCartById(customer.getCartId());
		return new ResponseEntity<CartDto>(cart, HttpStatus.OK);
	}

	@GetMapping(value = "/validateCart")
	public ResponseEntity<String> validateCart() {
		CustomerDto customer = customerService.getCurrentCustomer();
		cartService.validateCart(customer.getCartId());
		return new ResponseEntity<String>("Your cart is validated!", HttpStatus.OK);
	}

}
