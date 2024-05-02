package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.ProductDto;

public interface ProductService {

	ProductDto addProduct(ProductDto product);

	ProductDto getProductById(Integer productId);

	ProductDto updateProduct(ProductDto product, Integer productId);

	void deleteProduct(Integer productId);

	List<ProductDto> listAll();

	List<ProductDto> listAllByCategoryId(Integer categoryId);

	void deleteProductsByCategoryId(Integer categoryId);
}
