package com.example.cluvrnotifications.domain.notification.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;

@Document(collection = "notification_cache")
@Getter
@NoArgsConstructor
public class NotificationDocument {

	@Id
	private String id;    //MongoDB는 id 가 objectId -> hex문자열이라서 String으로 했습니다

	private Long receiverId;
	private NotificationType notificationType;
	private String content;
	private NotiTargetType targetType;
	private Long targetId;

	public NotificationDocument(Long receiverId, NotificationType notificationType, String content,
		NotiTargetType targetType, Long targetId) {
		this.receiverId = receiverId;
		this.notificationType = notificationType;
		this.content = content;
		this.targetType = targetType;
		this.targetId = targetId;
	}
}
