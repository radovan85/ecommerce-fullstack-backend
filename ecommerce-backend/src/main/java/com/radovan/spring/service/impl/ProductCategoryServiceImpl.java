package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.entity.ProductCategoryEntity;
import com.radovan.spring.exceptions.InstanceNotExistException;
import com.radovan.spring.repository.ProductCategoryRepository;
import com.radovan.spring.repository.ProductRepository;
import com.radovan.spring.service.ProductCategoryService;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductCategoryRepository categoryRepository;

	@Autowired
	private TempConverter tempConverter;
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public ProductCategoryDto addCategory(ProductCategoryDto category) {
		// TODO Auto-generated method stub
		ProductCategoryEntity categoryEntity = tempConverter.categoryDtoToEntity(category);
		ProductCategoryEntity storedCategory = categoryRepository.save(categoryEntity);
		ProductCategoryDto returnValue = tempConverter.categoryEntityToDto(storedCategory);
		return returnValue;
	}

	@Override
	public ProductCategoryDto getCategoryById(Integer categoryId) {
		// TODO Auto-generated method stub
		ProductCategoryDto returnValue = null;
		Optional<ProductCategoryEntity> categoryOpt = categoryRepository.findById(categoryId);
		if (categoryOpt.isPresent()) {
			returnValue = tempConverter.categoryEntityToDto(categoryOpt.get());
		} else {
			Error error = new Error("Category not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public ProductCategoryDto updateCategory(Integer categoryId, ProductCategoryDto category) {
		// TODO Auto-generated method stub
		ProductCategoryDto returnValue = null;
		Optional<ProductCategoryEntity> categoryOpt = categoryRepository.findById(categoryId);
		if (categoryOpt.isPresent()) {
			category.setProductCategoryId(categoryId);
			ProductCategoryEntity categoryEntity = tempConverter.categoryDtoToEntity(category);
			ProductCategoryEntity updatedCategory = categoryRepository.saveAndFlush(categoryEntity);
			returnValue = tempConverter.categoryEntityToDto(updatedCategory);
		} else {
			Error error = new Error("Category not found!");
			throw new InstanceNotExistException(error);
		}
		return returnValue;
	}

	@Override
	public void deleteCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		Optional<ProductCategoryEntity> categoryOpt = categoryRepository.findById(categoryId);
		if (categoryOpt.isPresent()) {
			productRepository.deleteAllByCategoryId(categoryId);
			categoryRepository.deleteById(categoryId);
			categoryRepository.flush();
		} else {
			Error error = new Error("Category not found!");
			throw new InstanceNotExistException(error);
		}
	}

	@Override
	public List<ProductCategoryDto> listAll() {
		// TODO Auto-generated method stub
		List<ProductCategoryDto> returnValue = new ArrayList<ProductCategoryDto>();
		Optional<List<ProductCategoryEntity>> allCategoriesOpt = Optional.ofNullable(categoryRepository.findAll());
		if (!allCategoriesOpt.isEmpty()) {
			allCategoriesOpt.get().forEach((categoryEntity) -> {
				ProductCategoryDto categoryDto = tempConverter.categoryEntityToDto(categoryEntity);
				returnValue.add(categoryDto);
			});
		}
		return returnValue;
	}

}
