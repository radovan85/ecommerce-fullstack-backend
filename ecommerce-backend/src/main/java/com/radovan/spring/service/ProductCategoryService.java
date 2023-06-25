package com.radovan.spring.service;

import java.util.List;

import com.radovan.spring.dto.ProductCategoryDto;

public interface ProductCategoryService {

	ProductCategoryDto addCategory(ProductCategoryDto category);
	
	ProductCategoryDto getCategoryById(Integer categoryId);
	
	ProductCategoryDto updateCategory(Integer categoryId, ProductCategoryDto category);
	
	void deleteCategory(Integer categoryId);
	
	List<ProductCategoryDto> listAll();
}
