package com.radovan.spring.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegistrationForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = 30)
	private String firstName;

	@NotEmpty
	@Size(max = 30)
	private String lastName;

	@NotEmpty
	@Size(max = 40)
	@Email(regexp = ".+[@].+[\\.].+")
	private String email;

	@NotEmpty
	private String password;

	@NotEmpty
	@Size(max = 40)
	private String billingAddress;

	@NotEmpty
	@Size(max = 40)
	private String billingCity;

	@NotEmpty
	@Size(max = 40)
	private String billingState;

	@NotEmpty
	@Size(max = 10)
	private String billingPostcode;

	@NotEmpty
	@Size(max = 40)
	private String billingCountry;

	@NotEmpty
	@Size(max = 75)
	private String shippingAddress;

	@NotEmpty
	@Size(max = 40)
	private String shippingCity;

	@NotEmpty
	@Size(max = 40)
	private String shippingState;

	@NotEmpty
	@Size(max = 10)
	private String shippingPostcode;

	@NotEmpty
	@Size(max = 40)
	private String shippingCountry;

	@NotEmpty
	@Size(min=9,max = 15)
	private String customerPhone;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getBillingPostcode() {
		return billingPostcode;
	}

	public void setBillingPostcode(String billingPostcode) {
		this.billingPostcode = billingPostcode;
	}

	public String getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getShippingCity() {
		return shippingCity;
	}

	public void setShippingCity(String shippingCity) {
		this.shippingCity = shippingCity;
	}

	public String getShippingState() {
		return shippingState;
	}

	public void setShippingState(String shippingState) {
		this.shippingState = shippingState;
	}

	public String getShippingPostcode() {
		return shippingPostcode;
	}

	public void setShippingPostcode(String shippingPostcode) {
		this.shippingPostcode = shippingPostcode;
	}

	public String getShippingCountry() {
		return shippingCountry;
	}

	public void setShippingCountry(String shippingCountry) {
		this.shippingCountry = shippingCountry;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

}
