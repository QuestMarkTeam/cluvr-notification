package com.example.cluvrnotifications.domain.notification.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.entity.Notification;
import com.example.cluvrnotifications.domain.notification.entity.NotificationSetting;

/**
 * QueryDSL을 이용한 Notification 관련 복잡한 조회 쿼리 구현체
 *
 * - Spring Data JPA의 네이밍 방식으로 표현하기 어려운 조건을 커버
 * - Custom Repository와 연결됨 (NotificationRepositoryCustom)
 *
 *
 * @author escomputer
 */
public interface NotificationRepositoryCustom {

	/**
	 * 설명: 특정 사용자(userId)의 알림 목록을 페이징으로 조회함
	 *
	 * - isRead 값이 null일 경우 전체 알림 조회
	 * - isRead 값이 true/false일 경우 읽음/안 읽음 필터링 적용
	 *
	 * @param userId {사용자 식별자}
	 * @param isRead {읽음 여부 (null일 경우 전체)}
	 * @param pageable {페이지 및 정렬 정보}
	 * @return {페이징된 알림 목록}
	 *
	 * @author escomputer
	 */
	Page<ReadNotificationResponseDto> findAllDtosByUserId(Long userId, Boolean isRead, Pageable pageable);

	/**
	 * 설명: 알림 ID와 사용자 ID로 특정 알림 단건을 조회
	 *
	 * - 보안 상 다른 사용자의 알림 접근을 막기 위한 이중 조건
	 *
	 * @param id {알림 식별자}
	 * @param userId {사용자 식별자}
	 * @return Optional<Notification> {해당 조건에 맞는 알림 (없을 경우 빈 Optional)}
	 *
	 * @author escomputer
	 */

	Optional<Notification> findByIdAndUserId(Long id, Long userId);

	List<NotificationSetting> findAllByUserId(Long userId);

}

