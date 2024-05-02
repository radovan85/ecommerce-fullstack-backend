package com.radovan.spring.converter;

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

public abstract class TempConverter {

	public abstract CartDto cartEntityToDto(CartEntity cart);

	public abstract CartEntity cartDtoToEntity(CartDto cart);

	public abstract CartItemDto cartItemEntityToDto(CartItemEntity cartItem);

	public abstract CartItemEntity cartItemDtoToEntity(CartItemDto cartItem);

	public abstract CustomerDto customerEntityToDto(CustomerEntity customer);

	public abstract CustomerEntity customerDtoToEntity(CustomerDto customer);

	public abstract ShippingAddressDto shippingAddressEntityToDto(ShippingAddressEntity address);

	public abstract ShippingAddressEntity shippingAddressDtoToEntity(ShippingAddressDto address);

	public abstract RoleDto roleEntityToDto(RoleEntity role);

	public abstract RoleEntity roleDtoToEntity(RoleDto role);

	public abstract UserDto userEntityToDto(UserEntity user);

	public abstract UserEntity userDtoToEntity(UserDto user);

	public abstract ProductCategoryDto categoryEntityToDto(ProductCategoryEntity category);

	public abstract ProductCategoryEntity categoryDtoToEntity(ProductCategoryDto category);

	public abstract ProductDto productEntityToDto(ProductEntity product);

	public abstract ProductEntity productDtoToEntity(ProductDto product);

	public abstract ProductImageDto productImageEntityToDto(ProductImageEntity image);

	public abstract ProductImageEntity productImageDtoToEntity(ProductImageDto image);

	public abstract OrderAddressDto shippingAddressToOrderAddress(ShippingAddressDto shippingAddress);

	public abstract OrderAddressEntity orderAddressDtoToEntity(OrderAddressDto address);

	public abstract OrderAddressDto orderAddressEntityToDto(OrderAddressEntity address);

	public abstract OrderItemDto orderItemEntityToDto(OrderItemEntity orderItem);

	public abstract OrderItemEntity orderItemDtoToEntity(OrderItemDto orderItem);

	public abstract OrderDto orderEntityToDto(OrderEntity order);

	public abstract OrderEntity orderDtoToEntity(OrderDto order);

	public abstract OrderItemDto cartItemToOrderItemDto(CartItemDto cartItem);

}
