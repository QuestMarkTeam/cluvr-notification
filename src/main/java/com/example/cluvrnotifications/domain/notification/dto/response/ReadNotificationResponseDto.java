package com.example.cluvrnotifications.domain.notification.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;

@Getter
@NoArgsConstructor
public class ReadNotificationResponseDto {
	private String id;
	private NotificationType notificationType;
	private String title;
	private String content;
	private NotiTargetType targetType;
	private Long targetId;
	private Boolean isRead;
	private LocalDateTime createdAt;

	public ReadNotificationResponseDto(NotificationDocument document) {
		this.id = document.getId();
		this.notificationType = document.getNotificationType();
		this.title = document.getTitle();
		this.content = document.getContent();
		this.targetType = document.getTargetType();
		this.targetId = document.getTargetId();
		this.isRead = document.getIsRead();
		this.createdAt = document.getCreatedAt();
	}

	// 기존 생성자 유지 (호환성을 위해)
	public ReadNotificationResponseDto(Long id, String content, Boolean isRead) {
		this.id = String.valueOf(id);
		this.content = content;
		this.isRead = isRead;
	}
}
