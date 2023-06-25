package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.OrderDto;

public interface OrderService {

	OrderDto addOrder();

	OrderDto getOrderById(Integer orderId);

	void deleteOrder(Integer orderId);

	List<OrderDto> listAll();

	Float calculateGrandTotal(Integer orderId);

}
