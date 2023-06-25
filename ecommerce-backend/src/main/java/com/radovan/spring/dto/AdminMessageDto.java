package com.radovan.spring.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminMessageDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer adminMessageId;

	@NotEmpty
	@Size(max = 255)
	private String text;

	private String createdAtStr;

	private Integer customerId;

	public Integer getAdminMessageId() {
		return adminMessageId;
	}

	public void setAdminMessageId(Integer adminMessageId) {
		this.adminMessageId = adminMessageId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCreatedAtStr() {
		return createdAtStr;
	}

	public void setCreatedAtStr(String createdAtStr) {
		this.createdAtStr = createdAtStr;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

}
