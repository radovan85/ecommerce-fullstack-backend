package com.radovan.spring.controller;

import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.dto.ProductImageDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.ProductImageService;
import com.radovan.spring.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductImageService imageService;

	@GetMapping(value = "/allProducts")
	public ResponseEntity<List<ProductDto>> listAllProducts() {
		return ResponseEntity.ok().body(productService.listAll());
	}

	@GetMapping(value = "/allProducts/{categoryId}")
	public ResponseEntity<List<ProductDto>> listAllProductsByCategoryId(
			@PathVariable("categoryId") Integer categoryId) {
		return ResponseEntity.ok().body(productService.listAllByCategoryId(categoryId));
	}

	@GetMapping(value = "/productDetails/{productId}")
	public ResponseEntity<ProductDto> getProductDetails(@PathVariable("productId") Integer productId) {
		return ResponseEntity.ok().body(productService.getProductById(productId));
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeProduct")
	public ResponseEntity<String> storeProduct(@Validated @RequestBody ProductDto product, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The data has not been validated!"));
		}

		ProductDto storedProduct = productService.addProduct(product);
		return ResponseEntity.ok().body("The product with id " + storedProduct.getProductId() + " has been stored!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateProduct/{productId}")
	public ResponseEntity<String> updateProduct(@Validated @RequestBody ProductDto product,
			@PathVariable("productId") Integer productId, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException(new Error("The data has not been validated!"));
		}

		ProductDto updatedProduct = productService.updateProduct(product, productId);
		return ResponseEntity.ok()
				.body("The product with id " + updatedProduct.getProductId() + " has been updated without any issues!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteProduct/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable("productId") Integer productId) {
		productService.deleteProduct(productId);
		return ResponseEntity.ok().body("The product with id " + productId + " has been permanently deleted!");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeImage/{productId}")
	public ResponseEntity<String> storeImage(@RequestPart("file") MultipartFile file,
			@PathVariable("productId") Integer productId) {
		imageService.addImage(file, productId);
		return ResponseEntity.ok().body("The image has been added without any issues!");
	}

	@GetMapping(value = "/allImages")
	public ResponseEntity<List<ProductImageDto>> getAllImages() {
		List<ProductImageDto> allImages = imageService.listAll();
		return ResponseEntity.ok().body(allImages);
	}
}
