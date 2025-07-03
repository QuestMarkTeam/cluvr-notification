package com.example.cluvrnotifications.domain.notification.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;


@Document(collection = "notifications")
@Getter
@NoArgsConstructor
public class NotificationDocument {

	@Id
	private String id;

	private Long receiverId;
	private NotificationType notificationType;
	private String title;
	private String content;
	private NotiTargetType targetType;
	private Long targetId;
	private Boolean isRead;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	public NotificationDocument(Long receiverId, NotificationType notificationType, String title, String content,
		NotiTargetType targetType, Long targetId) {
		this.receiverId = receiverId;
		this.notificationType = notificationType;
		this.title = title;
		this.content = content;
		this.targetType = targetType;
		this.targetId = targetId;
		this.isRead = false; // 기본값은 읽지 않음
	}

	public void markAsRead() {
		this.isRead = true;
	}

}