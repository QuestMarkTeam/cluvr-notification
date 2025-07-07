package com.example.cluvrnotifications.domain.notification.repository.base;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;

public interface NotificationCacheRepository extends MongoRepository<NotificationDocument, String> {

	/**
	 * 설명: 특정 사용자의 모든 알림을 페이징으로 조회
	 *
	 * @param receiverId 사용자 ID
	 * @param pageable 페이징 정보
	 * @return 페이징된 알림 목록
	 */
	Page<NotificationDocument> findByReceiverId(Long receiverId, Pageable pageable);

	/**
	 * 설명: 특정 사용자의 읽음 상태별 알림을 페이징으로 조회
	 *
	 * @param receiverId 사용자 ID
	 * @param isRead 읽음 상태
	 * @param pageable 페이징 정보
	 * @return 페이징된 알림 목록
	 */
	Page<NotificationDocument> findByReceiverIdAndIsRead(Long receiverId, Boolean isRead, Pageable pageable);

	/**
	 * 설명: 특정 사용자의 읽지 않은 알림만 조회
	 *
	 * @param receiverId 사용자 ID
	 * @return 읽지 않은 알림 목록
	 */
	List<NotificationDocument> findByReceiverIdAndIsReadFalse(Long receiverId);

	/**
	 * 설명: 특정 알림을 사용자 ID와 함께 조회 (권한 확인용)
	 *
	 * @param id 알림 ID
	 * @param receiverId 사용자 ID
	 * @return 알림 Optional
	 */
	Optional<NotificationDocument> findByIdAndReceiverId(String id, Long receiverId);


	/**
	 * 설명: 여러 알림을 ID로 일괄 삭제
	 *
	 * @param ids 삭제할 알림 ID 목록
	 */
	void deleteAllById(List<String> ids);
}
