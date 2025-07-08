package com.example.cluvrnotifications.domain.notification.service;

import org.springframework.stereotype.Service;

import com.example.cluvrnotifications.domain.notification.manager.NotificationListenerManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationQueueService {

	private final NotificationListenerManager notificationListenerManager;

	public void initQueueIfAbsent(Long userId) {
		notificationListenerManager.createQueueIfNotExists(userId);
	}


}
