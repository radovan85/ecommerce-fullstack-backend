package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.UserService;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private UserService userService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public CartItemDto addCartItem(Integer productId) {
		// TODO Auto-generated method stub
		CartItemDto returnValue = null;
		UserDto authUser = userService.getCurrentUser();
		CustomerEntity customerEntity = null;
		CartItemDto cartItem = new CartItemDto();
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(authUser.getId()));
		if (customerOpt.isPresent()) {
			customerEntity = customerOpt.get();
			Optional<ProductEntity> productOpt = productRepository.findById(productId);
			if (productOpt.isPresent()) {
				ProductEntity productEntity = productOpt.get();
				cartItem.setProductId(productEntity.getProductId());
				CartEntity cartEntity = customerEntity.getCart();
				cartItem.setCartId(cartEntity.getCartId());
				List<CartItemEntity> allCartItems = cartEntity.getCartItems();

				for (CartItemEntity tempItem : allCartItems) {
					if (tempItem.getProduct().getProductId().intValue() == productId) {
						cartItem.setCartItemId(tempItem.getCartItemId());
						cartItem.setQuantity(tempItem.getQuantity() + 1);
						cartItem.setPrice(tempItem.getPrice() + productEntity.getProductPrice());
						CartItemEntity cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
						CartItemEntity storedItem = cartItemRepository.save(cartItemEntity);
						cartService.refreshCartState(cartEntity.getCartId());
						returnValue = tempConverter.cartItemEntityToDto(storedItem);
						return returnValue;
					}
				}

				cartItem.setQuantity(1);
				cartItem.setPrice(productEntity.getProductPrice());
				CartItemEntity cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem);
				CartItemEntity storedItem = cartItemRepository.save(cartItemEntity);
				cartService.refreshCartState(cartEntity.getCartId());
				returnValue = tempConverter.cartItemEntityToDto(storedItem);
			} else {
				Error error = new Error("Product not found");
				throw new InstanceNotExistException(error);
			}
		} else {
			Error error = new Error("Customer not found!");
			throw new InstanceNotExistException(error);
		}

		return returnValue;
	}

	@Override
	public void deleteCartItem(Integer cartItemId) {
		// TODO Auto-generated method stub
		Optional<CartItemEntity> cartItemOpt = cartItemRepository.findById(cartItemId);
		if (cartItemOpt.isPresent()) {
			Integer cartId = cartItemOpt.get().getCart().getCartId();
			cartItemRepository.removeItem(cartItemId);
			cartItemRepository.flush();
			cartService.refreshCartState(cartId);
		} else {
			Error error = new Error("Item not found");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public void removeAllCartItems() {
		// TODO Auto-generated method stub
		UserDto authUser = userService.getCurrentUser();
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(authUser.getId()));
		if (customerOpt.isPresent()) {
			CustomerEntity customerEntity = customerOpt.get();
			Optional<CartEntity> cartOpt = cartRepository.findById(customerEntity.getCart().getCartId());
			if (cartOpt.isPresent()) {
				Integer cartId = cartOpt.get().getCartId();
				cartItemRepository.removeAllItems(cartId);
				cartItemRepository.flush();
				cartService.refreshCartState(cartId);
			} else {
				Error error = new Error("Invalid cart");
				throw new InvalidCartException(error);
			}
		} else {
			Error error = new Error("Invalid customer");
			throw new InvalidCartException(error);
		}
	}

	@Override
	public List<CartItemDto> listMyItems() {
		// TODO Auto-generated method stub
		List<CartItemDto> returnValue = new ArrayList<CartItemDto>();
		CartDto cart = cartService.getMyCart();
		Optional<List<CartItemEntity>> allItemsOpt = Optional
				.ofNullable(cartItemRepository.findAllByCartId(cart.getCartId()));
		if (!allItemsOpt.isEmpty()) {
			allItemsOpt.get().forEach((cartItem) -> {
				CartItemDto cartItemDto = tempConverter.cartItemEntityToDto(cartItem);
				returnValue.add(cartItemDto);
			});
		}

		return returnValue;
	}

	@Override
	public void removeAllByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		cartItemRepository.removeAllItems(cartId);
		cartItemRepository.flush();
		cartService.refreshCartState(cartId);
	}

	@Override
	public void removeAllByProductId(Integer productId) {
		// TODO Auto-generated method stub
		cartItemRepository.removeAllByProductId(productId);
		cartItemRepository.flush();

		List<CartEntity> allCarts = cartRepository.findAll();
		if (!allCarts.isEmpty()) {
			allCarts.forEach((cartEntity) -> {
				cartService.refreshCartState(cartEntity.getCartId());
			});
		}
	}

}
