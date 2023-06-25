package com.radovan.spring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.OrderAddressRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.service.OrderAddressService;

@Service
@Transactional
public class OrderAddressServiceImpl implements OrderAddressService {

	@Autowired
	private OrderAddressRepository addressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public OrderAddressDto getAddressByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		OrderAddressDto returnValue = null;
		Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isPresent()) {
			Optional<OrderAddressEntity> addressOpt = addressRepository
					.findById(orderOpt.get().getAddress().getOrderAddressId());
			if (addressOpt.isPresent()) {
				returnValue = tempConverter.orderAddressEntityToDto(addressOpt.get());
			} else {
				Error error = new Error("Address not found!");
				throw new InstanceNotExistException(error);
			}
		} else {
			Error error = new Error("Order not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

}
