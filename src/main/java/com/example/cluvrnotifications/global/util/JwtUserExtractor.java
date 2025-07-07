package com.example.cluvrnotifications.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.example.cluvrnotifications.external.client.ApiServerClient;

/**
 * 설명: JWT에서 사용자 정보를 추출하는 유틸리티 클래스
 * 
 * JWT 구조 변경으로 custom:userId가 제거됨
 * → sub를 통해 API 서버에서 userId 조회하는 방식으로 변경
 *
 * @author escomputer
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUserExtractor {

	private final ApiServerClient apiServerClient;

	/**
	 * 설명: JWT에서 userId를 추출
	 * JWT 구조 변경으로 custom:userId가 제거됨 → sub를 통해 API 서버에서 조회
	 *
	 * @param jwt JWT 토큰
	 * @return 사용자 ID
	 */
	public Long extractUserId(Jwt jwt) {
		// JWT에서 sub 추출 후 API 서버에서 userId 조회
		String sub = jwt.getSubject();
		log.debug("JWT Sub: {}", sub); // sub 값 출력하여 올바르게 추출되는지 확인
		return apiServerClient.getUserIdBySub(sub);
	}
}
