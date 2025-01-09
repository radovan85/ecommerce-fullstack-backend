package com.radovan.spring.service.impl;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.ProductService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private ProductService productService;

	private DecimalFormat decfor = new DecimalFormat("0.00");

	@Override
	@Transactional(readOnly = true)
	public CartDto getCartById(Integer cartId) {
		CartEntity cartEntity = cartRepository.findById(cartId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The cart has not been found")));

		return tempConverter.cartEntityToDto(cartEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public CartDto validateCart(Integer cartId) {
		CartDto cart = getCartById(cartId);
		if (cart.getCartItemsIds().isEmpty()) {
			throw new InvalidCartException(new Error("Your cart is currently empty!"));
		}
		return cart;
	}

	@Override
	@Transactional(readOnly = true)
	public Float calculateGrandTotal(Integer cartId) {
		Float grandTotal = 0f;
		List<CartItemDto> allCartItems = cartItemService.listAllByCartId(cartId);
		for (CartItemDto item : allCartItems) {
			ProductDto product = productService.getProductById(item.getProductId());
			grandTotal += (product.getProductPrice() * item.getQuantity());
		}
		return Float.valueOf(decfor.format(grandTotal));
	}

	@Override
	@Transactional
	public void refreshCartState(Integer cartId) {
		CartDto cart = getCartById(cartId);
		Float cartPrice = cartRepository.calculateCartPrice(cartId).orElse(0f);
		cart.setCartPrice(cartPrice);
		cartRepository.saveAndFlush(tempConverter.cartDtoToEntity(cart));
	}

	@Override
	@Transactional
	public void refreshAllCarts() {
		List<CartEntity> allCarts = cartRepository.findAll();
		allCarts.forEach(cartEntity -> refreshCartState(cartEntity.getCartId()));
	}

}
