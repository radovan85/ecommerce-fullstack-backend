package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.ProductCategoryService;

@RestController
@RequestMapping(value = "/api/categories")
public class ProductCategoryController {

	@Autowired
	private ProductCategoryService categoryService;

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeCategory")
	public ResponseEntity<String> addCategory(@Validated @RequestBody ProductCategoryDto category, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Data is not validated");
			throw new DataNotValidatedException(error);
		}

		categoryService.addCategory(category);
		return ResponseEntity.ok().body("Category is stored!");
	}

	@GetMapping(value = "/allCategories")
	public ResponseEntity<List<ProductCategoryDto>> getAllCategories() {
		List<ProductCategoryDto> allCategories = categoryService.listAll();
		return ResponseEntity.ok().body(allCategories);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateCategory/{categoryId}")
	public ResponseEntity<String> updateCategory(@Validated @PathVariable("categoryId") Integer categoryId,
			@RequestBody ProductCategoryDto category, Errors errors) {

		if (errors.hasErrors()) {
			Error error = new Error("Data is not validated!");
			throw new DataNotValidatedException(error);
		}

		ProductCategoryDto updatedCategory = categoryService.updateCategory(categoryId, category);
		return ResponseEntity.ok().body("Category with id " + updatedCategory.getProductCategoryId() + " is updated!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCategory/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok().body("Category with id " + categoryId + " is permanently deleted!");
	}

	@GetMapping(value = "/categoryDetails/{categoryId}")
	public ResponseEntity<ProductCategoryDto> getCategoryDetails(@PathVariable("categoryId") Integer categoryId) {
		ProductCategoryDto category = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok().body(category);
	}
}
