package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.BillingAddressDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.RegistrationForm;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.BillingAddressEntity;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.ExistingEmailException;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.AdminMessageRepository;
import com.radovan.spring.repository.BillingAddressRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.ShippingAddressRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.CustomerService;
import com.radovan.spring.service.UserService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BillingAddressRepository billingAddressRepository;

	@Autowired
	private ShippingAddressRepository shippingAddressRepository;

	@Autowired
	private AdminMessageRepository messageRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public CustomerDto storeCustomer(RegistrationForm form) {
		// TODO Auto-generated method stub
		CustomerDto customer = new CustomerDto();
		customer.setCustomerPhone(form.getCustomerPhone());

		Optional<UserEntity> userOpt = Optional.ofNullable(userRepository.findByEmail(form.getEmail()));
		if (userOpt.isPresent()) {
			Error error = new Error("Existing exists already");
			throw new ExistingEmailException(error);
		}

		RoleEntity roleEntity = roleRepository.findByRole("ROLE_USER");

		UserDto userDto = new UserDto();
		userDto.setFirstName(form.getFirstName());
		userDto.setLastName(form.getLastName());
		userDto.setEmail(form.getEmail());
		userDto.setPassword(passwordEncoder.encode(form.getPassword()));
		userDto.setEnabled((byte) 1);
		List<Integer> rolesIds = new ArrayList<Integer>();
		rolesIds.add(roleEntity.getId());
		userDto.setRolesIds(rolesIds);
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		UserEntity storedUser = userRepository.save(userEntity);

		Optional<List<UserEntity>> usersOpt = Optional.ofNullable(roleEntity.getUsers());
		List<UserEntity> users = new ArrayList<UserEntity>();
		if (!userOpt.isEmpty()) {
			users = usersOpt.get();
		}
		users.add(storedUser);
		roleEntity.setUsers(users);
		roleRepository.saveAndFlush(roleEntity);

		CartEntity cartEntity = new CartEntity();
		cartEntity.setCartPrice(0f);
		CartEntity storedCart = cartRepository.save(cartEntity);

		BillingAddressDto billingAddress = new BillingAddressDto();
		billingAddress.setAddress(form.getBillingAddress());
		billingAddress.setCity(form.getBillingCity());
		billingAddress.setCountry(form.getBillingCountry());
		billingAddress.setPostcode(form.getBillingPostcode());
		billingAddress.setState(form.getBillingState());

		BillingAddressEntity billAddressEntity = tempConverter.billingAddressDtoToEntity(billingAddress);
		BillingAddressEntity storedBillingAddress = billingAddressRepository.save(billAddressEntity);

		ShippingAddressDto shippingAddress = new ShippingAddressDto();
		shippingAddress.setAddress(form.getShippingAddress());
		shippingAddress.setCity(form.getShippingCity());
		shippingAddress.setCountry(form.getShippingCountry());
		shippingAddress.setPostcode(form.getShippingPostcode());
		shippingAddress.setState(form.getShippingState());

		ShippingAddressEntity shippAddressEntity = tempConverter.shippingAddressDtoToEntity(shippingAddress);
		ShippingAddressEntity storedShippingAddress = shippingAddressRepository.save(shippAddressEntity);

		customer.setBillingAddressId(storedBillingAddress.getBillingAddressId());
		customer.setCartId(storedCart.getCartId());
		customer.setShippingAddressId(storedShippingAddress.getShippingAddressId());
		customer.setUserId(storedUser.getId());

		CustomerEntity customerEntity = tempConverter.customerDtoToEntity(customer);
		CustomerEntity storedCustomer = customerRepository.save(customerEntity);

		storedCart.setCustomer(storedCustomer);
		cartRepository.saveAndFlush(storedCart);

		storedBillingAddress.setCustomer(storedCustomer);
		billingAddressRepository.saveAndFlush(storedBillingAddress);

		storedShippingAddress.setCustomer(storedCustomer);
		shippingAddressRepository.saveAndFlush(storedShippingAddress);

		CustomerDto returnValue = tempConverter.customerEntityToDto(storedCustomer);
		return returnValue;
	}

	@Override
	public List<CustomerDto> listAll() {
		// TODO Auto-generated method stub
		List<CustomerDto> returnValue = new ArrayList<CustomerDto>();
		Optional<List<CustomerEntity>> allCustomers = Optional.ofNullable(customerRepository.findAll());
		if (!allCustomers.isEmpty()) {
			allCustomers.get().forEach((customer) -> {
				CustomerDto customerDto = tempConverter.customerEntityToDto(customer);
				returnValue.add(customerDto);
			});
		}
		return returnValue;
	}

	@Override
	public CustomerDto getCustomerById(Integer customerId) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOpt = customerRepository.findById(customerId);
		if (customerOpt.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOpt.get());
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public CustomerDto getCustomerByUserId(Integer userId) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(userId));
		if (customerOpt.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOpt.get());
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		Optional<CustomerEntity> customerOpt = customerRepository.findById(customerId);
		if (customerOpt.isPresent()) {
			CustomerEntity customerEntity = customerRepository.findById(customerId).get();
			messageRepository.deleteAllByCustomerId(customerEntity.getCustomerId());
			Integer userId = customerEntity.getUser().getId();
			List<OrderEntity> allOrders = orderRepository.listAllByCustomerId(customerId);
			if (!allOrders.isEmpty()) {
				allOrders.forEach((orderEntity) -> {
					orderRepository.deleteById(orderEntity.getOrderId());
				});
			}

			customerRepository.deleteById(customerId);
			userRepository.clearUserRoles(userId);
			userRepository.deleteById(userId);
			userRepository.flush();
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public CustomerDto getCurrentCustomer() {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		UserDto authUser = userService.getCurrentUser();
		Optional<CustomerEntity> customerOpt = Optional.ofNullable(customerRepository.findByUserId(authUser.getId()));
		if (customerOpt.isPresent()) {
			returnValue = tempConverter.customerEntityToDto(customerOpt.get());
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public CustomerDto updateCustomer(Integer customerId, CustomerDto customer) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = null;
		Optional<CustomerEntity> customerOpt = customerRepository.findById(customerId);
		if (customerOpt.isPresent()) {
			customer.setCustomerId(customerId);
			CustomerEntity customerEntity = tempConverter.customerDtoToEntity(customer);
			CustomerEntity updatedCustomer = customerRepository.saveAndFlush(customerEntity);
			returnValue = tempConverter.customerEntityToDto(updatedCustomer);
		} else {
			Error error = new Error("Customer not found");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

}
