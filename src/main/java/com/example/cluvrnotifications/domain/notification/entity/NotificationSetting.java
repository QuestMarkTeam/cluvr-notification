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

import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false, length = 30)
	private NotificationType notificationType;

	@Column(name = "is_enabled", nullable = false)
	private Boolean isEnabled;

	public NotificationSetting(User user, NotificationType notificationType, Boolean isEnabled) {
		this.user = user;
		this.notificationType = notificationType;
		this.isEnabled = isEnabled;
	}

	public void updateStatus(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
