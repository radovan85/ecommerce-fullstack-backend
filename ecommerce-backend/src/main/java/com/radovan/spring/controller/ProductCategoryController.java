package com.radovan.spring.controller;

import com.radovan.spring.dto.ProductCategoryDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/categories")
public class ProductCategoryController {

	@Autowired
	private ProductCategoryService categoryService;

	@GetMapping(value = "/allCategories")
	public ResponseEntity<List<ProductCategoryDto>> getAllCategories() {
		return ResponseEntity.ok().body(categoryService.listAll());
	}

	@GetMapping(value = "/categoryDetails/{categoryId}")
	public ResponseEntity<ProductCategoryDto> getCategoryDetails(@PathVariable("categoryId") Integer categoryId) {
		return ResponseEntity.ok().body(categoryService.getCategoryById(categoryId));
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeCategory")
	public ResponseEntity<String> storeCategory(@Validated @RequestBody ProductCategoryDto category, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The data has not been validated!"));
		}

		ProductCategoryDto storedCategory = categoryService.addCategory(category);
		return ResponseEntity.ok()
				.body("The category with id " + storedCategory.getProductCategoryId() + " has been stored!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateCategory/{categoryId}")
	public ResponseEntity<String> updateCategory(@PathVariable("categoryId") Integer categoryId,
			@Validated @RequestBody ProductCategoryDto category, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The data has not been validated!"));
		}

		ProductCategoryDto updatedCategory = categoryService.updateCategory(category, categoryId);
		return ResponseEntity.ok().body("The category with id " + updatedCategory.getProductCategoryId()
				+ " has been updated without any issues!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteCategory/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok().body("The category with id " + categoryId + " has been permanently deleted!");
	}
}
