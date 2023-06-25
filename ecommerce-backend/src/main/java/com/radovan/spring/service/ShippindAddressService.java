package com.radovan.spring.service;

import com.radovan.spring.dto.ShippingAddressDto;

public interface ShippindAddressService {

	ShippingAddressDto retrieveShippingAddress();
	
	ShippingAddressDto updateShippingAddress(Integer addressId,ShippingAddressDto address);
}
