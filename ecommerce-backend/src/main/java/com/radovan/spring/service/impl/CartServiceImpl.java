package com.radovan.spring.service.impl;

import java.text.DecimalFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.exceptions.InvalidCartException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.UserService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerRepository customerRepository;

	private DecimalFormat decfor = new DecimalFormat("0.00");

	@Override
	public CartDto getCartByCartId(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto returnValue = null;
		Optional<CartEntity> cartOpt = cartRepository.findById(cartId);
		if (cartOpt.isPresent()) {
			returnValue = tempConverter.cartEntityToDto(cartOpt.get());
		} else {
			Error error = new Error("Invalid cart");
			throw new InvalidCartException(error);
		}
		return returnValue;
	}

	@Override
	public void refreshCartState(Integer cartId) {
		// TODO Auto-generated method stub
		CartEntity cartEntity = cartRepository.findById(cartId).get();
		Optional<Float> priceOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId));
		if (priceOpt.isPresent()) {
			Float price = priceOpt.get();
			price = Float.valueOf(decfor.format(price));
			cartEntity.setCartPrice(price);
		} else {
			cartEntity.setCartPrice(0f);
		}
		cartRepository.saveAndFlush(cartEntity);
	}

	@Override
	public Float calculateGrandTotal(Integer cartId) {
		// TODO Auto-generated method stub
		Optional<Float> grandTotalOpt = Optional.ofNullable(cartItemRepository.calculateGrandTotal(cartId));
		Float returnValue = 0f;

		if (grandTotalOpt.isPresent()) {
			Float grandTotal = grandTotalOpt.get();
			returnValue = Float.valueOf(decfor.format(grandTotal));
		}

		return returnValue;
	}

	@Override
	public Float calculateFullPrice(Integer cartId) {
		// TODO Auto-generated method stub
		Optional<Float> fullPriceOpt = Optional.ofNullable(cartItemRepository.calculateFullPrice(cartId));
		Float returnValue = 0f;

		if (fullPriceOpt.isPresent()) {
			Float fullPrice = fullPriceOpt.get();
			returnValue = Float.valueOf(decfor.format(fullPrice));
		}

		return returnValue;
	}

	@Override
	public CartDto validateCart(Integer cartId) {
		// TODO Auto-generated method stub
		CartDto returnValue = null;
		Optional<CartEntity> cartOpt = cartRepository.findById(cartId);
		Error error = new Error("Invalid Cart");

		if (cartOpt.isPresent()) {
			if (cartOpt.get().getCartItems().size() == 0) {
				throw new InvalidCartException(error);
			}

			returnValue = tempConverter.cartEntityToDto(cartOpt.get());

		} else {
			throw new InvalidCartException(error);
		}

		return returnValue;
	}

	@Override
	public CartDto getMyCart() {
		// TODO Auto-generated method stub
		CartDto returnValue = null;
		UserDto authUser = userService.getCurrentUser();
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(authUser.getId()));
		if (customerOpt.isPresent()) {
			CustomerEntity customerEntity = customerOpt.get();
			Optional<CartEntity> cartOpt = cartRepository.findById(customerEntity.getCart().getCartId());
			if (cartOpt.isPresent()) {
				returnValue = tempConverter.cartEntityToDto(cartOpt.get());
			} else {
				Error error = new Error("Invalid cart");
				throw new InvalidCartException(error);
			}
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

}
