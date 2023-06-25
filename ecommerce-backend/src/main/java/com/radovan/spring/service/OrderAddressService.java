package com.radovan.spring.service;

import com.radovan.spring.dto.OrderAddressDto;

public interface OrderAddressService {

	OrderAddressDto getAddressByOrderId(Integer orderId);
}
