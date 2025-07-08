package com.example.cluvrnotifications.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrnotifications.domain.notification.dto.request.QueueInitRequestDto;
import com.example.cluvrnotifications.domain.notification.service.NotificationQueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/queue")
public class NotificationQueueController {

	private final NotificationQueueService notificationQueueService;

	@PostMapping("/init")
	public ResponseEntity<Void> initQueue(@RequestBody QueueInitRequestDto requestDto) {
		notificationQueueService.initQueueIfAbsent(requestDto.getUserId());
		return ResponseEntity.ok().build();
	}
}
