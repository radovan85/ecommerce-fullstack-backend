package com.radovan.spring.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.ShippingAddressRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.OrderService;
import com.radovan.spring.service.UserService;
import com.radovan.spring.utils.RegistrationForm;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ShippingAddressRepository addressRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Override
	@Transactional
	public CustomerDto addCustomer(RegistrationForm form) {
		RoleEntity roleEntity = roleRepository.findByRole("ROLE_USER")
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The role has not been found!")));

		UserEntity userEntity = createUserEntity(form.getUser(), roleEntity);
		CartEntity cartEntity = createCartEntity();
		ShippingAddressEntity addressEntity = createShippingAddressEntity(form.getShippingAddress());
		CustomerEntity customerEntity = createCustomerEntity(form.getCustomer(), userEntity, cartEntity, addressEntity);
		cartEntity.setCustomer(customerEntity);
		cartRepository.save(cartEntity);

		return tempConverter.customerEntityToDto(customerRepository.save(customerEntity));
	}

	private UserEntity createUserEntity(UserDto userDto, RoleEntity roleEntity) {
		Optional<UserEntity> userOptional = userRepository.findByEmail(userDto.getEmail());
		if (userOptional.isPresent()) {
			throw new ExistingInstanceException(new Error("Email already exists!"));
		}
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userEntity.setEnabled((byte) 1);
		userEntity.setRoles(List.of(roleEntity));
		return userRepository.save(userEntity);
	}

	private CartEntity createCartEntity() {
		CartEntity cartEntity = new CartEntity();
		cartEntity.setCartPrice(0f);
		return cartRepository.save(cartEntity);
	}

	private ShippingAddressEntity createShippingAddressEntity(ShippingAddressDto addressDto) {
		ShippingAddressEntity addressEntity = tempConverter.shippingAddressDtoToEntity(addressDto);
		return addressRepository.save(addressEntity);
	}

	private CustomerEntity createCustomerEntity(CustomerDto customerDto, UserEntity userEntity, CartEntity cartEntity,
			ShippingAddressEntity addressEntity) {
		CustomerEntity customerEntity = tempConverter.customerDtoToEntity(customerDto);
		customerEntity.setShippingAddress(addressEntity);
		customerEntity.setCart(cartEntity);
		customerEntity.setUser(userEntity);
		return customerEntity;
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerById(Integer customerId) {
		return customerRepository.findById(customerId).map(tempConverter::customerEntityToDto)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The customer has not been found!")));
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCustomerByUserId(Integer userId) {
		return customerRepository.findByUserId(userId).map(tempConverter::customerEntityToDto)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The customer has not been found!")));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerDto> listAll() {
		return customerRepository.findAll().stream().map(tempConverter::customerEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDto getCurrentCustomer() {
		// TODO Auto-generated method stub
		UserDto currentUser = userService.getCurrentUser();
		return getCustomerByUserId(currentUser.getId());
	}

	@Override
	@Transactional
	public CustomerDto updtadeCustomer(CustomerDto customer) {
		// TODO Auto-generated method stub
		CustomerDto currentCustomer = getCurrentCustomer();
		customer.setCartId(currentCustomer.getCartId());
		customer.setCustomerId(currentCustomer.getCustomerId());
		customer.setShippingAddressId(currentCustomer.getShippingAddressId());
		customer.setUserId(currentCustomer.getUserId());
		CustomerEntity updatedCustomer = customerRepository.saveAndFlush(tempConverter.customerDtoToEntity(customer));
		return tempConverter.customerEntityToDto(updatedCustomer);
	}

	@Override
	@Transactional
	public void removeCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto customer = getCustomerById(customerId);
		List<OrderDto> allOrders = orderService.listAllByCartId(customer.getCartId());
		allOrders.forEach((order) -> {
			orderService.deleteOrder(order.getOrderId());
		});
		customerRepository.deleteById(customerId);
		customerRepository.flush();
	}

}
