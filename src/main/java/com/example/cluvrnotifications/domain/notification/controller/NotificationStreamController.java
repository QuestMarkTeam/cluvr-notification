package com.example.cluvrnotifications.domain.notification.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.cluvrnotifications.common.annotation.Auth;
import com.example.cluvrnotifications.common.dto.AuthUser;
import com.example.cluvrnotifications.domain.notification.service.NotificationStreamService;

/**
 * 설명: SSE 연결 요청을 처리하고, MongoDB에 저장된 알림을 실시간으로 전송하는 컨트롤러
 *
 *
 * @author escomputer
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationStreamController {

	private final NotificationStreamService notificationStreamService;

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
	public SseEmitter connect(@Auth AuthUser authUser) {
		Long userId = authUser.id();
		return notificationStreamService.connect(userId);
	}
}
