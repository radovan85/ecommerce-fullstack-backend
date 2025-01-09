package com.radovan.spring.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.exceptions.OutOfStockException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.ProductService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository itemRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartService cartService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ProductService productService;

	@Override
	@Transactional
	public CartItemDto addCartItem(Integer productId) {
		CustomerDto customer = customerService.getCurrentCustomer();
		CartDto cart = cartService.getCartById(customer.getCartId());
		ProductDto product = productService.getProductById(productId);

		Optional<CartItemDto> existingItem = listAllByCartId(cart.getCartId()).stream()
				.filter(item -> item.getProductId().equals(productId)).findFirst();

		CartItemDto cartItem;
		if (existingItem.isPresent()) {
			cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + 1);
		} else {
			cartItem = new CartItemDto();
			cartItem.setProductId(productId);
			cartItem.setCartId(customer.getCartId());
			cartItem.setQuantity(1);
		}

		if (product.getUnitStock() < cartItem.getQuantity()) {
			throw new OutOfStockException(new Error(
					"There is a shortage of " + product.getProductName() + " in stock!"));
		}

		CartItemEntity cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
		cartItemEntity.setCart(tempConverter.cartDtoToEntity(cart));
		CartItemEntity storedItem = itemRepository.save(cartItemEntity);
		cartService.refreshCartState(cart.getCartId());
		return tempConverter.cartItemEntityToDto(storedItem);
	}

	@Override
	@Transactional
	public void removeCartItem(Integer itemId) {
		CustomerDto currentCustomer = customerService.getCurrentCustomer();
		CartItemDto cartItem = getItemById(itemId);

		if (!currentCustomer.getCartId().equals(cartItem.getCartId())) {
			throw new OperationNotAllowedException(new Error("Operation not allowed!"));
		}

		itemRepository.removeItem(itemId);
		cartService.refreshCartState(currentCustomer.getCartId());
	}

	@Override
	@Transactional
	public void removeAllByCartId(Integer cartId) {
		itemRepository.removeAllByCartId(cartId);
		cartService.refreshCartState(cartId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByCartId(Integer cartId) {
		List<CartItemEntity> allItems = itemRepository.findAllByCartId(cartId);
		return allItems.stream().map(tempConverter::cartItemEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CartItemDto getItemById(Integer itemId) {
		CartItemEntity itemEntity = itemRepository.findById(itemId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The item has not been found")));
		return tempConverter.cartItemEntityToDto(itemEntity);
	}

	@Override
	@Transactional
	public void removeAllByProductId(Integer productId) {
		// TODO Auto-generated method stub
		productService.getProductById(productId);
		itemRepository.removeAllByProductId(productId);
		itemRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartItemDto> listAllByProductId(Integer productId) {
		// TODO Auto-generated method stub
		List<CartItemEntity> allItems = itemRepository.findAllByProductId(productId);
		return allItems.stream().map(tempConverter::cartItemEntityToDto).collect(Collectors.toList());
	}

}
