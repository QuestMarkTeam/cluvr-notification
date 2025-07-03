package com.example.cluvrnotifications.domain.notification.repository.base;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cluvrnotifications.domain.notification.entity.NotificationSettingDocument;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;

/**
 * 설명: 알림 설정을 위한 MongoDB Repository
 *
 * @author escomputer
 */
public interface NotificationSettingRepository extends MongoRepository<NotificationSettingDocument, String> {

	/**
	 * 설명: 특정 사용자의 모든 알림 설정 조회
	 *
	 * @param userId 사용자 ID
	 * @return 알림 설정 목록
	 */
	List<NotificationSettingDocument> findAllByUserId(Long userId);

	/**
	 * 설명: 특정 사용자의 특정 알림 타입 설정 조회
	 *
	 * @param userId 사용자 ID
	 * @param notificationType 알림 타입
	 * @return 알림 설정 Optional
	 */
	Optional<NotificationSettingDocument> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);
}
