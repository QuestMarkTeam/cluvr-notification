package com.example.cluvrnotifications.domain.notification.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

	private Long receiverId;
	private NotificationType type;
	private String content;
	private NotiTargetType targetType;
	private Long targetId;

	public static NotificationEvent from(
		Long receiverId,
		NotificationType type,
		String content,
		NotiTargetType targetType,
		Long targetId
	) {
		return new NotificationEvent(receiverId, type, content, targetType, targetId);
	}
}
