package com.radovan.spring.converter;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.CartDto;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.CustomerDto;
import com.radovan.spring.dto.OrderAddressDto;
import com.radovan.spring.dto.OrderDto;
import com.radovan.spring.dto.OrderItemDto;
import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ProductImageDto;
import com.radovan.spring.dto.RoleDto;
import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.CartEntity;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.CustomerEntity;
import com.radovan.spring.entity.OrderAddressEntity;
import com.radovan.spring.entity.OrderEntity;
import com.radovan.spring.entity.OrderItemEntity;
import com.radovan.spring.entity.ProductCategoryEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.entity.ProductImageEntity;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.ShippingAddressEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.CartRepository;
import com.radovan.spring.repository.CustomerRepository;
import com.radovan.spring.repository.OrderItemRepository;
import com.radovan.spring.repository.OrderRepository;
import com.radovan.spring.repository.ProductCategoryRepository;
import com.radovan.spring.repository.ProductImageRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.ShippingAddressRepository;
import com.radovan.spring.repository.UserRepository;

@Component
public class TempConverterExtension extends TempConverter {

	private ModelMapper mapper;

	private CustomerRepository customerRepository;

	private CartItemRepository cartItemRepository;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private ShippingAddressRepository shippingAddressRepository;

	private CartRepository cartRepository;

	private ProductImageRepository imageRepository;

	private ProductCategoryRepository categoryRepository;

	private ProductRepository productRepository;

	private OrderRepository orderRepository;

	private OrderItemRepository orderItemRepository;

	private DecimalFormat decfor = new DecimalFormat("0.00");

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private ZoneId zoneId = ZoneId.of("UTC");

	@Override
	public CartDto cartEntityToDto(CartEntity cart) {
		CartDto returnValue = mapper.map(cart, CartDto.class);
		Optional<CustomerEntity> customerOptional = Optional.ofNullable(cart.getCustomer());
		if (customerOptional.isPresent()) {
			returnValue.setCustomerId(customerOptional.get().getCustomerId());
		}

		Optional<List<CartItemEntity>> cartItemsOptional = Optional.ofNullable(cart.getCartItems());
		if (!cartItemsOptional.isEmpty()) {
			List<Integer> cartItemsIds = cartItemsOptional.get().stream().map(CartItemEntity::getCartItemId)
					.collect(Collectors.toList());
			returnValue.setCartItemsIds(cartItemsIds);
		}

		returnValue.setCartPrice(Float.valueOf(decfor.format(returnValue.getCartPrice())));

		return returnValue;
	}

	public CartEntity cartDtoToEntity(CartDto cart) {
		CartEntity returnValue = mapper.map(cart, CartEntity.class);
		Optional<Integer> customerIdOptional = Optional.ofNullable(cart.getCartId());
		if (customerIdOptional.isPresent()) {
			Integer customerId = customerIdOptional.get();
			CustomerEntity customerEntity = customerRepository.findById(customerId).orElse(null);
			returnValue.setCustomer(customerEntity);
		}

		Optional<List<Integer>> cartItemsIdsOptional = Optional.ofNullable(cart.getCartItemsIds());
		List<CartItemEntity> cartItems = new ArrayList<CartItemEntity>();
		if (!cartItemsIdsOptional.isEmpty()) {
			cartItemsIdsOptional.get().forEach((itemId) -> {
				Optional<CartItemEntity> itemOptional = cartItemRepository.findById(itemId);
				if (itemOptional.isPresent()) {
					cartItems.add(itemOptional.get());
				}
			});

		}

		returnValue.setCartItems(cartItems);
		returnValue.setCartPrice(Float.valueOf(decfor.format(returnValue.getCartPrice())));
		return returnValue;
	}

	@Override
	public RoleDto roleEntityToDto(RoleEntity role) {
		// TODO Auto-generated method stub
		RoleDto returnValue = mapper.map(role, RoleDto.class);
		List<Integer> usersIds = new ArrayList<Integer>();
		Optional<List<UserEntity>> usersOptional = Optional.ofNullable(role.getUsers());
		if (!usersOptional.isEmpty()) {
			usersOptional.get().forEach((userEntity) -> {
				usersIds.add(userEntity.getId());
			});
		}
		returnValue.setUsersIds(usersIds);
		return returnValue;
	}

	@Override
	public RoleEntity roleDtoToEntity(RoleDto role) {
		// TODO Auto-generated method stub
		RoleEntity returnValue = mapper.map(role, RoleEntity.class);
		List<UserEntity> users = new ArrayList<UserEntity>();
		Optional<List<Integer>> usersIdsOptional = Optional.ofNullable(role.getUsersIds());
		if (!usersIdsOptional.isEmpty()) {
			usersIdsOptional.get().forEach((userId) -> {
				UserEntity userEntity = userRepository.findById(userId).orElse(null);
				if (userEntity != null) {
					users.add(userEntity);
				}
			});
		}

		returnValue.setUsers(users);
		return returnValue;
	}

	@Override
	public UserDto userEntityToDto(UserEntity user) {
		// TODO Auto-generated method stub
		UserDto returnValue = mapper.map(user, UserDto.class);
		List<Integer> rolesIds = new ArrayList<Integer>();
		Optional<List<RoleEntity>> rolesOptional = Optional.ofNullable(user.getRoles());
		if (!rolesOptional.isEmpty()) {
			rolesIds = rolesOptional.get().stream().map(RoleEntity::getId).collect(Collectors.toList());
		}

		returnValue.setRolesIds(rolesIds);

		Optional<Byte> enabledOptional = Optional.ofNullable(user.getEnabled());
		if (enabledOptional.isPresent()) {
			returnValue.setEnabled(enabledOptional.get().shortValue());
		}
		return returnValue;
	}

	@Override
	public UserEntity userDtoToEntity(UserDto user) {
		// TODO Auto-generated method stub
		UserEntity returnValue = mapper.map(user, UserEntity.class);
		List<RoleEntity> roles = new ArrayList<RoleEntity>();
		Optional<List<Integer>> rolesIdsOptional = Optional.ofNullable(user.getRolesIds());
		if (!rolesIdsOptional.isEmpty()) {
			rolesIdsOptional.get().forEach((roleId) -> {
				RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
				if (roleEntity != null) {
					roles.add(roleEntity);
				}
			});
		}

		returnValue.setRoles(roles);

		Optional<Short> enabledOptional = Optional.ofNullable(user.getEnabled());
		if (enabledOptional.isPresent()) {
			returnValue.setEnabled(enabledOptional.get().byteValue());
		}
		return returnValue;
	}

	@Override
	public CustomerDto customerEntityToDto(CustomerEntity customer) {
		// TODO Auto-generated method stub
		CustomerDto returnValue = mapper.map(customer, CustomerDto.class);
		Optional<ShippingAddressEntity> addressOptional = Optional.ofNullable(customer.getShippingAddress());
		if (addressOptional.isPresent()) {
			returnValue.setShippingAddressId(addressOptional.get().getShippingAddressId());
		}

		Optional<CartEntity> cartOptional = Optional.ofNullable(customer.getCart());
		if (cartOptional.isPresent()) {
			returnValue.setCartId(cartOptional.get().getCartId());
		}

		Optional<UserEntity> userOptional = Optional.ofNullable(customer.getUser());
		if (userOptional.isPresent()) {
			returnValue.setUserId(userOptional.get().getId());
		}

		return returnValue;
	}

	@Override
	public CustomerEntity customerDtoToEntity(CustomerDto customer) {
		// TODO Auto-generated method stub
		CustomerEntity returnValue = mapper.map(customer, CustomerEntity.class);

		Optional<Integer> addressIdOptional = Optional.ofNullable(customer.getShippingAddressId());
		if (addressIdOptional.isPresent()) {
			Integer addressId = addressIdOptional.get();
			ShippingAddressEntity addressEntity = shippingAddressRepository.findById(addressId).orElse(null);
			returnValue.setShippingAddress(addressEntity);
		}

		Optional<Integer> cartIdOptional = Optional.ofNullable(customer.getCartId());
		if (cartIdOptional.isPresent()) {
			Integer cartId = cartIdOptional.get();
			CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
			returnValue.setCart(cartEntity);
		}

		Optional<Integer> userIdOptional = Optional.ofNullable(customer.getUserId());
		if (userIdOptional.isPresent()) {
			Integer userId = userIdOptional.get();
			UserEntity userEntity = userRepository.findById(userId).orElse(null);
			returnValue.setUser(userEntity);
		}

		return returnValue;
	}

	@Override
	public ShippingAddressDto shippingAddressEntityToDto(ShippingAddressEntity address) {
		// TODO Auto-generated method stub
		ShippingAddressDto returnValue = mapper.map(address, ShippingAddressDto.class);
		Optional<CustomerEntity> customerOptional = Optional.ofNullable(address.getCustomer());
		if (customerOptional.isPresent()) {
			returnValue.setCustomerId(customerOptional.get().getCustomerId());
		}

		return returnValue;
	}

	@Override
	public ShippingAddressEntity shippingAddressDtoToEntity(ShippingAddressDto address) {
		// TODO Auto-generated method stub
		ShippingAddressEntity returnValue = mapper.map(address, ShippingAddressEntity.class);
		Optional<Integer> customerIdOptional = Optional.ofNullable(address.getCustomerId());
		if (customerIdOptional.isPresent()) {
			Integer customerId = customerIdOptional.get();
			CustomerEntity customerEntity = customerRepository.findById(customerId).orElse(null);
			returnValue.setCustomer(customerEntity);
		}

		return returnValue;
	}

	@Override
	public ProductCategoryDto categoryEntityToDto(ProductCategoryEntity category) {
		// TODO Auto-generated method stub

		return mapper.map(category, ProductCategoryDto.class);
	}

	@Override
	public ProductCategoryEntity categoryDtoToEntity(ProductCategoryDto category) {
		// TODO Auto-generated method stub

		return mapper.map(category, ProductCategoryEntity.class);
	}

	@Override
	public ProductDto productEntityToDto(ProductEntity product) {
		// TODO Auto-generated method stub
		ProductDto returnValue = mapper.map(product, ProductDto.class);
		returnValue.setProductPrice(Float.valueOf(decfor.format(returnValue.getProductPrice())));
		returnValue.setDiscount(Float.valueOf(decfor.format(returnValue.getDiscount())));
		Optional<ProductImageEntity> imageOptional = Optional.ofNullable(product.getImage());
		if (imageOptional.isPresent()) {
			returnValue.setImageId(imageOptional.get().getId());
		}

		Optional<ProductCategoryEntity> categoryOptional = Optional.ofNullable(product.getProductCategory());
		if (categoryOptional.isPresent()) {
			returnValue.setProductCategoryId(categoryOptional.get().getProductCategoryId());
		}

		return returnValue;
	}

	@Override
	public ProductEntity productDtoToEntity(ProductDto product) {
		// TODO Auto-generated method stub
		ProductEntity returnValue = mapper.map(product, ProductEntity.class);
		returnValue.setProductPrice(Float.valueOf(decfor.format(returnValue.getProductPrice())));
		returnValue.setDiscount(Float.valueOf(decfor.format(returnValue.getDiscount())));
		Optional<Integer> imageIdOptional = Optional.ofNullable(product.getImageId());
		if (imageIdOptional.isPresent()) {
			Integer imageId = imageIdOptional.get();
			ProductImageEntity imageEntity = imageRepository.findById(imageId).orElse(null);
			returnValue.setImage(imageEntity);
		}

		Optional<Integer> categoryIdOptional = Optional.ofNullable(product.getProductCategoryId());
		if (categoryIdOptional.isPresent()) {
			Integer categoryId = categoryIdOptional.get();
			ProductCategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElse(null);
			returnValue.setProductCategory(categoryEntity);
		}

		return returnValue;
	}

	@Override
	public ProductImageDto productImageEntityToDto(ProductImageEntity image) {
		// TODO Auto-generated method stub
		ProductImageDto returnValue = mapper.map(image, ProductImageDto.class);
		Optional<ProductEntity> productOptional = Optional.ofNullable(image.getProduct());
		if (productOptional.isPresent()) {
			returnValue.setProductId(productOptional.get().getProductId());
		}

		return returnValue;
	}

	@Override
	public ProductImageEntity productImageDtoToEntity(ProductImageDto image) {
		// TODO Auto-generated method stub

		ProductImageEntity returnValue = mapper.map(image, ProductImageEntity.class);
		Optional<Integer> productIdOptional = Optional.ofNullable(image.getProductId());
		if (productIdOptional.isPresent()) {
			Integer productId = productIdOptional.get();
			ProductEntity productEntity = productRepository.findById(productId).orElse(null);
			if (productEntity != null) {
				returnValue.setProduct(productEntity);
			}
		}
		return returnValue;
	}

	@Override
	public CartItemDto cartItemEntityToDto(CartItemEntity cartItem) {
		// TODO Auto-generated method stub
		CartItemDto returnValue = mapper.map(cartItem, CartItemDto.class);
		Optional<ProductEntity> productOptional = Optional.ofNullable(cartItem.getProduct());
		if (productOptional.isPresent()) {
			returnValue.setProductId(productOptional.get().getProductId());
			Float discount = productOptional.get().getDiscount();
			Float productPrice = productOptional.get().getProductPrice();
			Float itemPrice = productPrice - ((productPrice * discount) / 100);
			itemPrice = itemPrice * cartItem.getQuantity();
			returnValue.setPrice(Float.valueOf(decfor.format(itemPrice)));
			returnValue.setProductId(productOptional.get().getProductId());

		}

		Optional<CartEntity> cartOptional = Optional.ofNullable(cartItem.getCart());
		if (cartOptional.isPresent()) {
			returnValue.setCartId(cartOptional.get().getCartId());
		}

		return returnValue;
	}

	@Override
	public CartItemEntity cartItemDtoToEntity(CartItemDto cartItem) {
		// TODO Auto-generated method stub
		CartItemEntity returnValue = mapper.map(cartItem, CartItemEntity.class);
		Optional<Integer> productIdOptional = Optional.ofNullable(cartItem.getProductId());
		if (productIdOptional.isPresent()) {
			Integer productId = productIdOptional.get();
			ProductEntity productEntity = productRepository.findById(productId).orElse(null);
			if (productEntity != null) {
				Float discount = productEntity.getDiscount();
				Float productPrice = productEntity.getProductPrice();
				Float itemPrice = productPrice - ((productPrice * discount) / 100);
				itemPrice = itemPrice * cartItem.getQuantity();
				returnValue.setPrice(Float.valueOf(decfor.format(itemPrice)));
				returnValue.setProduct(productEntity);

			}
		}

		Optional<Integer> cartIdOptional = Optional.ofNullable(cartItem.getCartId());
		if (cartIdOptional.isPresent()) {
			Integer cartId = cartIdOptional.get();
			CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
			if (cartEntity != null) {
				returnValue.setCart(cartEntity);
			}
		}

		return returnValue;
	}

	@Override
	public OrderAddressDto shippingAddressToOrderAddress(ShippingAddressDto shippingAddress) {
		// TODO Auto-generated method stub
		return mapper.map(shippingAddress, OrderAddressDto.class);
	}

	@Override
	public OrderAddressDto orderAddressEntityToDto(OrderAddressEntity address) {
		// TODO Auto-generated method stub
		OrderAddressDto returnValue = mapper.map(address, OrderAddressDto.class);
		Optional<OrderEntity> orderOptional = Optional.ofNullable(address.getOrder());
		if (orderOptional.isPresent()) {
			returnValue.setOrderId(orderOptional.get().getOrderId());
		}
		return returnValue;
	}

	@Override
	public OrderAddressEntity orderAddressDtoToEntity(OrderAddressDto address) {
		// TODO Auto-generated method stub
		OrderAddressEntity returnValue = mapper.map(address, OrderAddressEntity.class);
		Optional<Integer> orderIdOptional = Optional.ofNullable(address.getOrderId());
		if (orderIdOptional.isPresent()) {
			Integer orderId = orderIdOptional.get();
			OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
			if (orderEntity != null) {
				returnValue.setOrder(orderEntity);
			}
		}
		return returnValue;
	}

	@Override
	public OrderItemDto orderItemEntityToDto(OrderItemEntity orderItem) {
		// TODO Auto-generated method stub
		OrderItemDto returnValue = mapper.map(orderItem, OrderItemDto.class);
		Optional<OrderEntity> orderoOptional = Optional.ofNullable(orderItem.getOrder());
		if (orderoOptional.isPresent()) {
			returnValue.setOrderId(orderoOptional.get().getOrderId());
		}
		return returnValue;
	}

	@Override
	public OrderItemEntity orderItemDtoToEntity(OrderItemDto orderItem) {
		// TODO Auto-generated method stub
		OrderItemEntity returnValue = mapper.map(orderItem, OrderItemEntity.class);
		Optional<Integer> orderIdOptional = Optional.ofNullable(orderItem.getOrderId());
		if (orderIdOptional.isPresent()) {
			Integer orderId = orderIdOptional.get();
			OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
			if (orderEntity != null) {
				returnValue.setOrder(orderEntity);
			}
		}
		return returnValue;
	}

	@Override
	public OrderDto orderEntityToDto(OrderEntity order) {
		// TODO Auto-generated method stub
		OrderDto returnValue = mapper.map(order, OrderDto.class);
		List<Integer> orderItemsIds = new ArrayList<>();
		Optional<List<OrderItemEntity>> itemsOptional = Optional.ofNullable(order.getOrderedItems());
		if (!itemsOptional.isEmpty()) {
			itemsOptional.get().forEach((item) -> {
				orderItemsIds.add(item.getOrderItemId());
			});
		}

		returnValue.setOrderedItemsIds(orderItemsIds);

		Optional<OrderAddressEntity> addressOptional = Optional.ofNullable(order.getAddress());
		if (addressOptional.isPresent()) {
			returnValue.setAddressId(addressOptional.get().getOrderAddressId());
		}

		Optional<Timestamp> createdAtOptional = Optional.ofNullable(order.getCreatedAt());
		if (createdAtOptional.isPresent()) {
			ZonedDateTime createdAtZoned = createdAtOptional.get().toLocalDateTime().atZone(zoneId);
			String createdAtStr = createdAtZoned.format(formatter);
			returnValue.setCreatedAt(createdAtStr);
		}

		Optional<CartEntity> cartOptional = Optional.ofNullable(order.getCart());
		if (cartOptional.isPresent()) {
			returnValue.setCartId(cartOptional.get().getCartId());
		}
		return returnValue;
	}

	@Override
	public OrderEntity orderDtoToEntity(OrderDto order) {
		// TODO Auto-generated method stub
		OrderEntity returnValue = mapper.map(order, OrderEntity.class);
		Optional<List<Integer>> itemsIdsOptional = Optional.ofNullable(order.getOrderedItemsIds());
		List<OrderItemEntity> orderedItems = new ArrayList<>();
		if (!itemsIdsOptional.isEmpty()) {
			itemsIdsOptional.get().forEach((itemId) -> {
				OrderItemEntity itemEntity = orderItemRepository.findById(itemId).orElse(null);
				if (itemEntity != null) {
					orderedItems.add(itemEntity);
				}
			});
		}

		returnValue.setOrderedItems(orderedItems);

		Optional<Integer> cartIdOptional = Optional.ofNullable(order.getCartId());
		if (cartIdOptional.isPresent()) {
			Integer cartId = cartIdOptional.get();
			CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
			if (cartEntity != null) {
				returnValue.setCart(cartEntity);
			}
		}

		return returnValue;
	}

	@Override
	public OrderItemDto cartItemToOrderItemDto(CartItemDto cartItem) {
		// TODO Auto-generated method stub
		OrderItemDto returnValue = mapper.map(cartItem, OrderItemDto.class);
		Optional<Integer> productIdOptional = Optional.ofNullable(cartItem.getProductId());
		if (productIdOptional.isPresent()) {
			Integer productId = productIdOptional.get();
			ProductEntity productEntity = productRepository.findById(productId).orElse(null);
			if (productEntity != null) {
				returnValue.setProductDiscount(productEntity.getDiscount());
				returnValue.setProductName(productEntity.getProductName());
				returnValue.setProductPrice(productEntity.getProductPrice());
			}
		}
		return returnValue;
	}

	@Autowired
	private void injectAll(ModelMapper mapper, CustomerRepository customerRepository,
			CartItemRepository cartItemRepository, UserRepository userRepository, RoleRepository roleRepository,
			ShippingAddressRepository shippingAddressRepository, CartRepository cartRepository,
			ProductImageRepository imageRepository, ProductCategoryRepository categoryRepository,
			ProductRepository productRepository, OrderRepository orderRepository,
			OrderItemRepository orderItemRepository) {
		this.mapper = mapper;
		this.customerRepository = customerRepository;
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.shippingAddressRepository = shippingAddressRepository;
		this.cartRepository = cartRepository;
		this.imageRepository = imageRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;

	}

}
