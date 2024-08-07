package com.radovan.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.ShippingAddressDto;
import com.radovan.spring.service.ShippingAddressService;

@RestController
@RequestMapping(value = "/api/addresses")
public class ShippingAddressController {

	@Autowired
	private ShippingAddressService addressService;

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allAddresses")
	public ResponseEntity<List<ShippingAddressDto>> getAllAddresses() {
		List<ShippingAddressDto> addresses = addressService.listAll();
		return new ResponseEntity<>(addresses, HttpStatus.OK);
	}
}
