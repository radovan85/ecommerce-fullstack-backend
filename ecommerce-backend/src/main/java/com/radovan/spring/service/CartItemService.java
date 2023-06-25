package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.CartItemDto;

public interface CartItemService {

	CartItemDto addCartItem(Integer productId);
	
	void deleteCartItem(Integer cartItemId);
	
	void removeAllCartItems();
	
	List<CartItemDto> listMyItems();
	
	void removeAllByCartId(Integer cartId);
	
	void removeAllByProductId(Integer productId);
}
