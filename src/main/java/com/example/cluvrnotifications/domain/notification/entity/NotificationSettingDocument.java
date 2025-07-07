package com.example.cluvrnotifications.domain.notification.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cluvrnotifications.domain.notification.enums.NotificationType;

/**
 * 설명: 사용자별 알림 설정을 저장하는 MongoDB 문서
 *
 * @author escomputer
 */
@Document(collection = "notification_settings")
@CompoundIndex(def = "{'userId': 1, 'notificationType': 1}", unique = true)
@Getter
@NoArgsConstructor
public class NotificationSettingDocument {

	@Id
	private String id;

	private Long userId;
	private NotificationType notificationType;
	private Boolean isEnabled;

	public NotificationSettingDocument(Long userId, NotificationType notificationType, Boolean isEnabled) {
		this.userId = userId;
		this.notificationType = notificationType;
		this.isEnabled = isEnabled;
	}

	public void updateStatus(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
