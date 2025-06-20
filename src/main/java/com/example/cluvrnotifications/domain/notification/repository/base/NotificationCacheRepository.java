package com.example.cluvrnotifications.domain.notification.repository.base;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;

public interface NotificationCacheRepository extends MongoRepository<NotificationDocument, String> {

	/**
	 * 설명: 특정 사용자 ID(receiverId)에 해당하는 모든 NotificationDocument를 조회
	 *
	 * @param receiverId 알림 수신 대상 사용자 ID
	 * @return 해당 사용자의 알림 리스트
	 *
	 * @author escomputer
	 */
	List<NotificationDocument> findAllByReceiverId(Long receiverId);

	void deleteAllById(List<String> ids);

	List<NotificationDocument> findByReceiverId(Long receiverId);
}
