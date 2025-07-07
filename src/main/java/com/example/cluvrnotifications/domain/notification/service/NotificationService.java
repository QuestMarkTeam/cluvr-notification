package com.example.cluvrnotifications.domain.notification.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cluvrnotifications.common.dto.PageResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationsSettingResponseDto;
import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.entity.NotificationSettingDocument;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationSettingRepository;
import com.example.cluvrnotifications.global.exception.BusinessException;
import com.example.cluvrnotifications.global.response.ResponseCode;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationCacheRepository notificationCacheRepository;
	private final NotificationSettingRepository notificationSettingRepository;

	/**
	 * 설명: 특정 사용자의 알림 목록을 페이징하여 조회합니다.
	 */
	public PageResponseDto<ReadNotificationResponseDto> getNotifications(Long userId, int page, int size, Boolean isRead) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<NotificationDocument> result;
		
		if (isRead != null) {
			result = notificationCacheRepository.findByReceiverIdAndIsRead(userId, isRead, pageable);
		} else {
			result = notificationCacheRepository.findByReceiverId(userId, pageable);
		}
		
		Page<ReadNotificationResponseDto> dtoPage = result.map(ReadNotificationResponseDto::new);
		return PageResponseDto.toDto(dtoPage);
	}

	/**
	 * 설명: 특정 알림을 읽음 처리합니다.
	 */
	@Transactional
	public void markAsRead(Long userId, String notificationId) {
		NotificationDocument notification = notificationCacheRepository.findByIdAndReceiverId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		if (!notification.getIsRead()) {
			notification.markAsRead();
			notificationCacheRepository.save(notification);
		}
	}

	/**
	 * 설명: 특정 알림을 삭제합니다.
	 */
	@Transactional
	public void deleteNotification(Long userId, String notificationId) {
		NotificationDocument notification = notificationCacheRepository.findByIdAndReceiverId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		notificationCacheRepository.delete(notification);
	}

	/**
	 * 설명: 사용자의 알림 설정을 변경합니다.
	 */
	@Transactional
	public void updateSettings(Long userId, Map<NotificationType, Boolean> updates) {
		updates.forEach((notificationType, isEnabled) -> {
			NotificationSettingDocument setting = notificationSettingRepository
				.findByUserIdAndNotificationType(userId, notificationType)
				.orElse(new NotificationSettingDocument(userId, notificationType, true));

			setting.updateStatus(isEnabled);
			notificationSettingRepository.save(setting);
		});
	}

	/**
	 * 설명: 사용자의 현재 알림 설정 상태를 조회합니다.
	 */
	public ReadNotificationsSettingResponseDto getSettings(Long userId) {
		List<NotificationSettingDocument> settings = notificationSettingRepository.findAllByUserId(userId);
		
		if (settings.isEmpty()) {
			for (NotificationType type : NotificationType.values()) {
				settings.add(new NotificationSettingDocument(userId, type, true));
			}
			notificationSettingRepository.saveAll(settings);
		}
		
		return ReadNotificationsSettingResponseDto.from(settings);
	}

	/**
	 * 설명: 특정 알림의 상세정보를 조회합니다.
	 */
	public ReadNotificationResponseDto getNotification(Long userId, String notificationId) {
		NotificationDocument notification = notificationCacheRepository.findByIdAndReceiverId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		return new ReadNotificationResponseDto(notification);
	}
}
