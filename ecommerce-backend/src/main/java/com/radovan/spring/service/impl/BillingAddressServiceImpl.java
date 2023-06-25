package com.radovan.spring.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.BillingAddressDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.entity.BillingAddressEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.BillingAddressRepository;
import com.radovan.spring.service.BillingAddressService;
import com.radovan.spring.service.CustomerService;

@Service
@Transactional
public class BillingAddressServiceImpl implements BillingAddressService {

	@Autowired
	private BillingAddressRepository billingAddressRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerService customerService;

	@Override
	public BillingAddressDto retrieveBillingAddress() {
		// TODO Auto-generated method stub
		BillingAddressDto returnValue = null;
		CustomerDto customer = customerService.getCurrentCustomer();
		Optional<BillingAddressEntity> addressOpt = billingAddressRepository.findById(customer.getBillingAddressId());
		if (addressOpt.isPresent()) {
			returnValue = tempConverter.billingAddressEntityToDto(addressOpt.get());
		} else {
			Error error = new Error("Billing address not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public BillingAddressDto updateBillingAddress(Integer addressId, BillingAddressDto address) {
		// TODO Auto-generated method stub
		BillingAddressDto returnValue = null;
		Optional<BillingAddressEntity> addressOpt = billingAddressRepository.findById(addressId);
		if(addressOpt.isPresent()) {
			address.setBillingAddressId(addressId);
			BillingAddressEntity addressEntity = tempConverter.billingAddressDtoToEntity(address);
			BillingAddressEntity updatedAddress = billingAddressRepository.saveAndFlush(addressEntity);
			returnValue = tempConverter.billingAddressEntityToDto(updatedAddress);
		}else {
			Error error = new Error("Address not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

}
