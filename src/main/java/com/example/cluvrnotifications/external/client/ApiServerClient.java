package com.example.cluvrnotifications.external.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cluvrnotifications.external.dto.UserApiResponse;
import com.example.cluvrnotifications.global.exception.BusinessException;
import com.example.cluvrnotifications.global.response.ResponseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 설명: API 서버와 통신하여 사용자 정보를 가져오는 클라이언트
 *
 * @author escomputer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiServerClient {

	private final WebClient webClient;

	/**
	 * 설명: JWT의 sub 값을 통해 userId를 조회
	 *
	 * @param sub JWT의 subject 값
	 * @return 사용자 ID
	 * @throws BusinessException API 호출 실패 시
	 */
	public Long getUserIdBySub(String sub) {
		try {
			UserApiResponse response = webClient.get()
				.uri("/api/users/sub/{sub}/user-id", sub)
					.retrieve()
					.bodyToMono(UserApiResponse.class)
					.block();

			if (response == null || response.getData() == null) {
				throw new BusinessException(ResponseCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다.");
			}

			return response.getData();
		} catch (Exception e) {
			log.error("API 서버에서 사용자 ID 조회 실패: sub={}, error={}", sub, e.getMessage());
			throw new BusinessException(ResponseCode.USER_NOT_FOUND, "사용자 정보 조회에 실패했습니다.");
		}
	}
}