package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;

@RestController
@RequestMapping(value = "/api/cart")
public class CartController {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CartService cartService;

	@PostMapping(value = "/addCartItem/{productId}")
	public ResponseEntity<String> addCartItem(@PathVariable("productId") Integer productId) {
		cartItemService.addCartItem(productId);
		return ResponseEntity.ok().body("Item added to your cart");
	}

	@DeleteMapping(value = "/removeItem/{itemId}")
	public ResponseEntity<String> deleteItem(@PathVariable("itemId") Integer itemId) {
		cartItemService.deleteCartItem(itemId);
		return ResponseEntity.ok().body("Item removed from cart!");
	}

	@DeleteMapping(value = "/clearCart")
	public ResponseEntity<String> clearCart() {
		cartItemService.removeAllCartItems();
		return ResponseEntity.ok().body("Cart is cleared!");
	}

	@GetMapping(value = "/findMyCart")
	public ResponseEntity<CartDto> getMyCart() {
		CartDto cart = cartService.getMyCart();
		return ResponseEntity.ok().body(cart);
	}

	@GetMapping(value = "/listMyItems")
	public ResponseEntity<List<CartItemDto>> getMyItems() {
		List<CartItemDto> allCartItems = cartItemService.listMyItems();
		return ResponseEntity.ok().body(allCartItems);
	}

	@GetMapping(value = "/checkout")
	public ResponseEntity<String> checkout() {
		CartDto cart = cartService.getMyCart();
		cartService.validateCart(cart.getCartId());
		return ResponseEntity.ok("Checkout is processing...");
	}

}
