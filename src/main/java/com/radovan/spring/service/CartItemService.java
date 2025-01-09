package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.CartItemDto;

public interface CartItemService {

	CartItemDto addCartItem(Integer productId);

	void removeCartItem(Integer itemId);

	void removeAllByCartId(Integer cartId);

	void removeAllByProductId(Integer productId);

	List<CartItemDto> listAllByCartId(Integer cartId);

	List<CartItemDto> listAllByProductId(Integer productId);

	CartItemDto getItemById(Integer itemId);

}
