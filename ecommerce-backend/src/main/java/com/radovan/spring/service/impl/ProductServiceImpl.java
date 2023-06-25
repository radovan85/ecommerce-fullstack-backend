package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartService cartService;

	@Override
	public ProductDto addProduct(ProductDto product) {
		// TODO Auto-generated method stub
		ProductEntity productEntity = tempConverter.productDtoToEntity(product);
		ProductEntity storedProduct = productRepository.save(productEntity);
		ProductDto returnValue = tempConverter.productEntityToDto(storedProduct);
		return returnValue;
	}

	@Override
	public ProductDto getProductById(Integer productId) {
		// TODO Auto-generated method stub
		ProductDto returnValue = null;
		Optional<ProductEntity> productOpt = productRepository.findById(productId);
		if (productOpt.isPresent()) {
			returnValue = tempConverter.productEntityToDto(productOpt.get());
		} else {
			Error error = new Error("Product not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public ProductDto updateProduct(Integer productId, ProductDto product) {
		// TODO Auto-generated method stub
		ProductDto returnValue = null;
		Optional<ProductEntity> productOpt = productRepository.findById(productId);
		if (productOpt.isPresent()) {
			product.setProductId(productId);
			ProductEntity productEntity = tempConverter.productDtoToEntity(product);
			ProductEntity updatedProduct = productRepository.save(productEntity);
			returnValue = tempConverter.productEntityToDto(updatedProduct);
			List<CartItemEntity> allCartItems = cartItemRepository.listAllByProductId(productId);
			if (!allCartItems.isEmpty()) {
				allCartItems.forEach((itemEntity) -> {
					CartItemDto itemDto = tempConverter.cartItemEntityToDto(itemEntity);
					CartItemEntity reloadedEntity = tempConverter.cartItemDtoToEntity(itemDto);
					cartItemRepository.saveAndFlush(reloadedEntity);
					Integer cartId = itemDto.getCartId();
					cartService.refreshCartState(cartId);
				});
			}
		} else {
			Error error = new Error("Product not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public void deleteProduct(Integer productId) {
		// TODO Auto-generated method stub
		Optional<ProductEntity> productOpt = productRepository.findById(productId);
		if (productOpt.isPresent()) {
			productRepository.deleteById(productId);
			productRepository.flush();
		} else {
			Error error = new Error("Product not found!");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public List<ProductDto> listAll() {
		// TODO Auto-generated method stub
		List<ProductDto> returnValue = new ArrayList<ProductDto>();
		Optional<List<ProductEntity>> allProductOpt = Optional.ofNullable(productRepository.findAll());
		if (!allProductOpt.isEmpty()) {
			allProductOpt.get().forEach((productEntity) -> {
				ProductDto productDto = tempConverter.productEntityToDto(productEntity);
				returnValue.add(productDto);
			});
		}
		return returnValue;
	}

}
