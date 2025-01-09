package com.radovan.spring.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OutOfStockException;
import com.radovan.spring.repository.OrderAddressRepository;
import com.radovan.spring.repository.OrderItemRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.ProductService;
import com.radovan.spring.service.ShippingAddressService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderAddressRepository orderAddressRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CartService cartService;

	@Autowired
	private ShippingAddressService shippingAddressService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private ProductService productService;

	private ZoneId zoneId = ZoneId.of("UTC");

	@Override
	@Transactional
	public OrderDto addOrder() {
		CustomerDto customer = customerService.getCurrentCustomer();
		CartDto cart = cartService.getCartById(customer.getCartId());
		cartService.validateCart(cart.getCartId());

		OrderDto orderDto = new OrderDto();
		orderDto.setCartId(cart.getCartId());
		orderDto.setOrderPrice(cart.getCartPrice());

		ShippingAddressDto shippingAddress = shippingAddressService.getAddressById(customer.getShippingAddressId());
		OrderAddressDto orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress);
		OrderAddressEntity storedAddress = orderAddressRepository
				.save(tempConverter.orderAddressDtoToEntity(orderAddress));

		ZonedDateTime currentTime = Instant.now().atZone(zoneId);
		Timestamp currentTimeStamp = Timestamp.valueOf(currentTime.toLocalDateTime());

		OrderEntity orderEntity = tempConverter.orderDtoToEntity(orderDto);
		orderEntity.setAddress(storedAddress);
		orderEntity.setCreatedAt(currentTimeStamp);
		OrderEntity storedOrder = orderRepository.save(orderEntity);

		List<OrderItemEntity> orderedItems = new ArrayList<>();
		List<CartItemDto> cartItems = cartItemService.listAllByCartId(cart.getCartId());

		for (CartItemDto cartItemDto : cartItems) {
			ProductDto product = productService.getProductById(cartItemDto.getProductId());
			if (cartItemDto.getQuantity() > product.getUnitStock()) {
				throw new OutOfStockException(
						new Error("There is a shortage of " + product.getProductName() + " in stock"));
			} else {
				product.setUnitStock(product.getUnitStock() - cartItemDto.getQuantity());
				productService.updateProduct(product, product.getProductId());
			}
			OrderItemDto orderItemDto = tempConverter.cartItemToOrderItemDto(cartItemDto);
			OrderItemEntity orderItemEntity = tempConverter.orderItemDtoToEntity(orderItemDto);
			orderItemEntity.setOrder(storedOrder);
			orderedItems.add(orderItemRepository.save(orderItemEntity));
		}

		storedOrder.getOrderedItems().addAll(orderedItems);
		storedOrder = orderRepository.saveAndFlush(storedOrder);

		cartItemService.removeAllByCartId(cart.getCartId());
		cartService.refreshCartState(cart.getCartId());

		return tempConverter.orderEntityToDto(storedOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto getOrderById(Integer orderId) {
		OrderEntity orderEntity = orderRepository.findById(orderId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The order has not been found!")));
		return tempConverter.orderEntityToDto(orderEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAll() {
		List<OrderEntity> allOrders = orderRepository.findAll();
		return allOrders.stream().map(tempConverter::orderEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> listAllByCartId(Integer cartId) {
		List<OrderEntity> allOrders = orderRepository.findAllByCartId(cartId);
		return allOrders.stream().map(tempConverter::orderEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteOrder(Integer orderId) {
		getOrderById(orderId);
		orderRepository.deleteById(orderId);
		orderRepository.flush();
	}

}
