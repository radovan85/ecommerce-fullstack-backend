package com.radovan.spring.service;

import com.radovan.spring.dto.CartDto;

public interface CartService {

	CartDto getCartById(Integer cartId);

	CartDto validateCart(Integer cartId);

	Float calculateGrandTotal(Integer cartId);

	void refreshCartState(Integer cartId);

	void refreshAllCarts();
}
