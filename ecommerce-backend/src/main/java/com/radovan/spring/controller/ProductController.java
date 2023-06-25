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

import com.radovan.spring.dto.ProductDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.service.CartItemService;
import com.radovan.spring.service.ProductService;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CartItemService cartItemService;

	@GetMapping(value = "/allProducts")
	public ResponseEntity<List<ProductDto>> getAllProducts() {
		List<ProductDto> allProducts = productService.listAll();
		return ResponseEntity.ok().body(allProducts);
	}

	@GetMapping(value = "/productDetails/{productId}")
	public ResponseEntity<ProductDto> getProductDetails(@PathVariable("productId") Integer productId) {
		ProductDto product = productService.getProductById(productId);
		return ResponseEntity.ok().body(product);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PostMapping(value = "/storeProduct")
	public ResponseEntity<String> addProduct(@Validated @RequestBody ProductDto product, Errors errors) {
		if (errors.hasErrors()) {
			Error error = new Error("Product is not validated!");
			throw new DataNotValidatedException(error);
		}

		ProductDto storedProduct = productService.addProduct(product);
		return ResponseEntity.ok().body("Product stored to database with id " + storedProduct.getProductId());
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/updateProduct/{productId}")
	public ResponseEntity<String> updateProduct(@Validated @RequestBody ProductDto product, Errors errors,
			@PathVariable("productId") Integer productId) {

		if (errors.hasErrors()) {
			Error error = new Error("Product is not validated!");
			throw new DataNotValidatedException(error);
		}

		ProductDto updatedProduct = productService.updateProduct(productId, product);
		return ResponseEntity.ok().body("Product with id " + updatedProduct.getProductId() + " is updated");
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteProduct/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable("productId") Integer productId) {
		cartItemService.removeAllByProductId(productId);
		productService.deleteProduct(productId);
		return ResponseEntity.ok().body("Product with id " + productId + " is permanently deleted");
	}
}
