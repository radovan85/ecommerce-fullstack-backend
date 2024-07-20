package com.radovan.spring.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.CartItemDto;
import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.entity.CartItemEntity;
import com.radovan.spring.entity.ProductEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.CartItemRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.CartService;
import com.radovan.spring.service.ProductCategoryService;
import com.radovan.spring.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private ProductCategoryService categoryService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Override
	@Transactional
	public ProductDto addProduct(ProductDto product) {
		categoryService.getCategoryById(product.getProductCategoryId());
		ProductEntity storedProduct = productRepository.save(tempConverter.productDtoToEntity(product));
		return tempConverter.productEntityToDto(storedProduct);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductById(Integer productId) {
		ProductEntity productEntity = productRepository.findById(productId)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The product has not been found!")));
		return tempConverter.productEntityToDto(productEntity);
	}

	@Override
	@Transactional
	public ProductDto updateProduct(ProductDto product, Integer productId) {
		categoryService.getCategoryById(product.getProductCategoryId());
		ProductDto currentProduct = getProductById(productId);
		List<CartItemDto> allCartItems = cartItemService.listAllByProductId(productId);
		product.setProductId(currentProduct.getProductId());
		if (currentProduct.getImageId() != null) {
			product.setImageId(currentProduct.getImageId());
		}

		ProductEntity updatedProduct = productRepository.saveAndFlush(tempConverter.productDtoToEntity(product));
		if (!allCartItems.isEmpty()) {
			allCartItems.forEach((item) -> {
				CartItemEntity itemEntity = tempConverter.cartItemDtoToEntity(item);
				cartItemRepository.saveAndFlush(itemEntity);
			});
		}
		cartService.refreshAllCarts();
		return tempConverter.productEntityToDto(updatedProduct);
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId) {
		getProductById(productId);
		cartItemService.removeAllByProductId(productId);
		cartService.refreshAllCarts();
		productRepository.deleteById(productId);
		productRepository.flush();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAll() {
		List<ProductEntity> allProducts = productRepository.findAll();
		return allProducts.stream().map(tempConverter::productEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAllByCategoryId(Integer categoryId) {
		List<ProductEntity> allProducts = productRepository.findAllByCategoryId(categoryId);
		return allProducts.stream().map(tempConverter::productEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteProductsByCategoryId(Integer categoryId) {
		List<ProductDto> allProducts = listAllByCategoryId(categoryId);
		allProducts.forEach((product) -> {
			deleteProduct(product.getProductId());
		});
	}

}
