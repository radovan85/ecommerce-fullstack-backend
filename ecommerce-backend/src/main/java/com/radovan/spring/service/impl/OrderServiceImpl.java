package com.radovan.spring.service.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.exceptions.InsufficientStockException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.OrderAddressRepository;
import com.radovan.spring.repository.OrderItemRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private OrderAddressRepository orderAddressRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private CustomerService customerService;

	private DecimalFormat decfor = new DecimalFormat("0.00");

	@Override
	public OrderDto addOrder() {
		// TODO Auto-generated method stub
		CustomerDto customer = customerService.getCurrentCustomer();
		CustomerEntity customerEntity = tempConverter.customerDtoToEntity(customer);
		ShippingAddressEntity shippingAddress = customerEntity.getShippingAddress();
		CartEntity cartEntity = customerEntity.getCart();
		OrderEntity orderEntity = new OrderEntity();
		List<OrderItemEntity> orderedItems = new ArrayList<OrderItemEntity>();

		List<CartItemEntity> cartItems = cartEntity.getCartItems();
		if (!cartItems.isEmpty()) {
			cartItems.forEach((item) -> {
				Optional<ProductEntity> productEntity = Optional.ofNullable(item.getProduct());
				if (productEntity.isPresent()) {
					if (productEntity.get().getUnitStock() < item.getQuantity()) {
						Error error = new Error("Not enough stock");
						throw new InsufficientStockException(error);
					} else {
						ProductEntity tempProduct = productEntity.get();
						Integer newStock = tempProduct.getUnitStock() - item.getQuantity();
						tempProduct.setUnitStock(newStock);
						productRepository.saveAndFlush(tempProduct);
					}
				}
			});
		}

		if (!cartItems.isEmpty()) {
			cartItems.forEach((item) -> {
				OrderItemEntity orderedItem = tempConverter.cartItemToOrderItemEntity(item);
				OrderItemEntity storedOrderedItem = orderItemRepository.save(orderedItem);
				orderedItems.add(storedOrderedItem);
			});
		}

		OrderAddressEntity orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress);
		OrderAddressEntity storedOrderAddress = orderAddressRepository.save(orderAddress);

		orderEntity.setCart(cartEntity);
		orderEntity.setCustomer(customerEntity);
		orderEntity.setOrderedItems(orderedItems);
		orderEntity.setAddress(storedOrderAddress);
		ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Belgrade"));
		Timestamp createdAt = Timestamp.from(currentTime.toInstant());
		orderEntity.setCreatedAt(createdAt);

		OrderEntity storedOrder = orderRepository.save(orderEntity);

		storedOrderAddress.setOrder(storedOrder);
		orderAddressRepository.saveAndFlush(storedOrderAddress);

		OrderDto returnValue = tempConverter.orderEntityToDto(storedOrder);

		storedOrder.getOrderedItems().forEach((item) -> {
			item.setOrder(storedOrder);
			orderItemRepository.saveAndFlush(item);
		});

		cartItemRepository.removeAllItems(cartEntity.getCartId());
		cartService.refreshCartState(cartEntity.getCartId());
		return returnValue;

	}

	@Override
	public OrderDto getOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		OrderDto returnValue = null;
		Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isPresent()) {
			returnValue = tempConverter.orderEntityToDto(orderOpt.get());
		} else {
			Error error = new Error("Order not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public void deleteOrder(Integer orderId) {
		// TODO Auto-generated method stub
		Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isPresent()) {
			orderRepository.deleteById(orderId);
			orderRepository.flush();
		} else {
			Error error = new Error("Order not found!");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public List<OrderDto> listAll() {
		// TODO Auto-generated method stub
		List<OrderDto> returnValue = new ArrayList<OrderDto>();
		Optional<List<OrderEntity>> allOrdersOpt = Optional.ofNullable(orderRepository.findAll());
		if (!allOrdersOpt.isEmpty()) {
			allOrdersOpt.get().forEach((orderEntity) -> {
				OrderDto orderDto = tempConverter.orderEntityToDto(orderEntity);
				returnValue.add(orderDto);
			});
		}
		return returnValue;
	}

	@Override
	public Float calculateGrandTotal(Integer orderId) {
		// TODO Auto-generated method stub
		Float returnValue = 0f;
		Optional<Float> grandTotalOpt = Optional.ofNullable(orderItemRepository.calculateGrandTotal(orderId));
		if (grandTotalOpt.isPresent()) {
			returnValue = Float.valueOf(decfor.format(grandTotalOpt.get()));
		}
		return returnValue;
	}

}
