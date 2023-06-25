package com.radovan.spring.dto;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

public class ProductDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer productId;

	@NotEmpty
	@Size(max = 90)
	private String productDescription;

	@NotEmpty
	@Size(max = 40)
	private String productStatus;

	@NotEmpty
	@Size(max = 40)
	private String productBrand;

	@NotEmpty
	@Size(max = 40)
	private String productModel;

	@NotEmpty
	@Size(max = 40)
	private String productName;

	@NotNull
	@DecimalMin(value = "10.0")
	private Float productPrice;

	@NotNull
	@Range(min = 0L)
	private Integer unitStock;

	@NotNull
	@DecimalMin(value = "0")
	private Float discount;

	@NotEmpty
	@Size(max = 255)
	private String imageUrl;

	private Integer productCategoryId;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public String getProductModel() {
		return productModel;
	}

	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Float productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getUnitStock() {
		return unitStock;
	}

	public void setUnitStock(Integer unitStock) {
		this.unitStock = unitStock;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Integer productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	@Override
	public String toString() {
		return "ProductDto [productId=" + productId + ", productDescription=" + productDescription + ", productStatus="
				+ productStatus + ", productBrand=" + productBrand + ", productModel=" + productModel + ", productName="
				+ productName + ", productPrice=" + productPrice + ", unitStock=" + unitStock + ", discount=" + discount
				+ ", imageUrl=" + imageUrl + ", productCategoryId=" + productCategoryId + "]";
	}
	
	

}
