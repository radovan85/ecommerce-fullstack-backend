package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.utils.RegistrationForm;

public interface CustomerService {

	CustomerDto addCustomer(RegistrationForm form);

	CustomerDto getCustomerById(Integer customerId);

	CustomerDto getCustomerByUserId(Integer userId);

	List<CustomerDto> listAll();

	CustomerDto getCurrentCustomer();

	CustomerDto updtadeCustomer(CustomerDto customer);

	void removeCustomer(Integer customerId);
}
