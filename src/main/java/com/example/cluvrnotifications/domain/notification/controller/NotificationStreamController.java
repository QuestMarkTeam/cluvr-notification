package com.example.cluvrnotifications.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.cluvrnotifications.common.annotation.Auth;
import com.example.cluvrnotifications.common.dto.AuthUser;
import com.example.cluvrnotifications.domain.notification.service.NotificationStreamService;
import com.example.cluvrnotifications.global.util.JwtUserExtractor;

/**
 * 설명: SSE 연결 요청을 처리하고, MongoDB에 저장된 알림을 실시간으로 전송하는 컨트롤러
 *
 *
 * @author escomputer
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/stream")
@CrossOrigin(
	origins = {"https://cluvr.co.kr", "https://www.cluvr.co.kr"},
	allowCredentials = "true"
)
public class NotificationStreamController {

	private final NotificationStreamService notificationStreamService;
	private final JwtUserExtractor jwtUserExtractor;

	/**
	 * 설명: 클라이언트에서 SSE 연결을 요청
	 *
	 *  최초 연결 시 Mongo에 저장된 알림도 함께 전송
	 *
	 * @param @Auth authUser 로그인된 사용자 ID
	 *
	 * @return SseEmitter 실시간 이벤트 스트림 연결 객체
	 *
	 * @author escomputer
	 */

	@GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect(@AuthenticationPrincipal Jwt jwt) {
		log.info("connect 시작");
		log.info(String.valueOf(jwt));
		Long userId = jwtUserExtractor.extractUserId(jwt);
		return notificationStreamService.connect(userId);
	}
}
