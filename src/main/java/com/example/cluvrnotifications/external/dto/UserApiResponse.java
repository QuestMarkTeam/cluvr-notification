package com.example.cluvrnotifications.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 설명: API 서버에서 사용자 정보 조회 시 사용하는 응답 DTO
 *
 * @author escomputer
 */
@Getter
@NoArgsConstructor
public class UserApiResponse {

	@JsonProperty("result")
	private ApiResult result;

	private Long data;

	@Getter
	@NoArgsConstructor
	public static class ApiResult {
		private int status;
		private String message;
	}
}