package com.example.cluvrnotifications.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.cluvrnotifications.common.entity.BaseTimeEntity;
import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.user.entity.User;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", length = 50, nullable = false)
	private NotificationType notificationType;

	@Column(length = 50, nullable = false)
	private String title;

	@Column(length = 100)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private NotiTargetType targetType;
	private Long targetId;

	@Column(nullable = false)
	private boolean isRead;

	public Notification(User user, NotificationType notificationType, String title, String content,
		NotiTargetType targetType, Long targetId, boolean isRead) {
		this.user = user;
		this.notificationType = notificationType;
		this.title = title;
		this.content = content;
		this.targetType = targetType;
		this.targetId = targetId;
		this.isRead = isRead;
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
