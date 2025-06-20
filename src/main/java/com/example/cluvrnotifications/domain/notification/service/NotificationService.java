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
import com.example.cluvrnotifications.domain.notification.entity.Notification;
import com.example.cluvrnotifications.domain.notification.entity.NotificationSetting;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationRepository;
import com.example.cluvrnotifications.domain.user.repository.UserRepository;
import com.example.cluvrnotifications.global.exception.BusinessException;
import com.example.cluvrnotifications.global.response.ResponseCode;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	/**
	 * 설명: 특정 사용자의 알림 목록을 페이징하여 조회합니다.
	 *
	 * @param userId 알림을 조회할 사용자 ID
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @param isRead 읽음 여부 필터 (null이면 전체)
	 * @return 페이징된 알림 목록 DTO
	 *
	 * @author escomputer
	 */

	public PageResponseDto<ReadNotificationResponseDto> getNotifications(Long userId, int page, int size,
		Boolean isRead) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<ReadNotificationResponseDto> result = notificationRepository.findAllDtosByUserId(userId, isRead, pageable);
		return PageResponseDto.toDto(result);

	}

	/**
	 * 설명: 특정 알림을 읽음 처리합니다.
	 *
	 * @param userId 사용자 ID
	 * @param notificationId 알림 ID
	 * @throws BusinessException 알림이 존재하지 않거나 권한이 없을 경우
	 *
	 * @author escomputer
	 */

	public void markAsRead(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		if (!notification.isRead()) {
			notification.markAsRead(); // 엔티티 내부 메서드에서 isRead = true 처리
		}
	}

	/**
	 * 설명: 특정 알림을 삭제합니다.
	 *
	 * <p>{추가적인 설명이 필요하다면 여기에 작성합니다.}
	 *
	 * @param userId 사용자 ID
	 * @param notificationId 알림 ID
	 * @throws BusinessException 알림이 존재하지 않거나 권한이 없을 경우
	 * @author escomputer
	 */

	public void deleteNotification(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		notificationRepository.delete(notification);

	}

	/**
	 * 설명: 사용자의 알림 설정을 변경합니다.
	 *
	 * @param userId 사용자 ID
	 * @param updates 알림 타입별 on/off 설정 맵
	 * @return 변경된 알림 설정 정보 DTO
	 * @author escomputer
	 */

	@Transactional
	public void updateSettings(Long userId,
		Map<NotificationType, Boolean> updates) {

		List<NotificationSetting> settingList = notificationRepository.findAllByUserId(userId);

		// 업데이트 적용
		settingList.forEach(setting -> {
			Boolean newValue = updates.get(setting.getNotificationType());
			if (newValue != null) {
				setting.updateStatus(newValue);
			}
		});

	}

	/**
	 * 설명: 사용자의 현재 알림 설정 상태를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 알림 설정 상태 DTO
	 *
	 * @author escomputer
	 */

	@Transactional(readOnly = true)
	public ReadNotificationsSettingResponseDto getSettings(Long userId) {
		List<NotificationSetting> settings = notificationRepository.findAllByUserId(userId);
		return ReadNotificationsSettingResponseDto.from(settings);
	}

	/**
	 * 설명: 특정 알림의 상세정보를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @param notificationId 알림 ID
	 * @return 알림 상세 정보 DTO
	 * @throws BusinessException 알림이 존재하지 않거나 권한이 없을 경우
	 *
	 * @author escomputer
	 */

	@Transactional(readOnly = true)
	public ReadNotificationResponseDto getNotification(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
			.orElseThrow(() -> new BusinessException(ResponseCode.NOTI_NOT_FOUND));

		return new ReadNotificationResponseDto(
			notification.getId(),
			notification.getContent(),
			notification.isRead()
		);
	}

}
