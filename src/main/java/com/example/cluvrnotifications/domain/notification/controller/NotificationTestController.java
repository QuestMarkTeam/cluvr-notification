package com.example.cluvrnotifications.domain.notification.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.cluvrnotifications.domain.notification.service.NotificationStreamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationTestController {

	private final NotificationStreamService notificationStreamService;

	/**
	 * 테스트용 SSE 연결api
	 */
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@RequestParam Long userId) {
		return notificationStreamService.connect(userId);
	}
}
