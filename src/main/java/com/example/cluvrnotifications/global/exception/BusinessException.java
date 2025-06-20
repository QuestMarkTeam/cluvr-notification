package com.example.cluvrnotifications.global.exception;

import lombok.Getter;

import com.example.cluvrnotifications.global.response.ResponseCode;

public class BusinessException extends RuntimeException {

	@Getter
	private final ResponseCode responseCode;

	public BusinessException(ResponseCode responseCode) {
		super(responseCode.getDefaultMessage());
		this.responseCode = responseCode;
	}

	public BusinessException(ResponseCode responseCode, String message) {
		super(message);
		this.responseCode = responseCode;
	}

}
