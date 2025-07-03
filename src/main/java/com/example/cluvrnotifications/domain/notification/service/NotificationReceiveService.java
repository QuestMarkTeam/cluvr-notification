package com.example.cluvrnotifications.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;

/**
 * 설명: NotificationEvent를 수신한 후, 알림을 MongoDB에 저장하고 SSE를 통해 전송하는 서비스
 *
 * @author escomputer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationReceiveService {

	private final NotificationSendService notificationSendService;
	private final NotificationCacheRepository notificationCacheRepository;

	public void receive(NotificationEvent event) {
		// 1. 모든 알림을 MongoDB에 저장 (isRead = false)
		NotificationDocument document = new NotificationDocument(
			event.getReceiverId(),
			event.getType(),
			generateTitle(event), // 제목 생성
			event.getContent(),
			event.getTargetType(),
			event.getTargetId()
		);
		
		NotificationDocument savedDocument = notificationCacheRepository.save(document);
		log.debug("알림 저장 완료: 받는 사람: {}, 내용: {}", event.getReceiverId(), event.getContent());

		// 2. SSE 전송 시도
		boolean sent = notificationSendService.send(event);

		if (sent) {
			// 3. 전송 성공 시 읽음 처리
			savedDocument.markAsRead();
			notificationCacheRepository.save(savedDocument);
			log.debug("SSE 전송 완료 -> 읽음 처리: 받는 사람: {}, 내용: {}", event.getReceiverId(), event.getContent());
		} else {
			log.debug("SSE 연결 없음 -> 읽지 않음 상태로 저장: 받는 사람: {}, 내용: {}", event.getReceiverId(), event.getContent());
		}
	}

	/**
	 * 설명: 알림 타입에 따라 제목을 생성
	 */
	private String generateTitle(NotificationEvent event) {
		return switch (event.getType()) {
			case COMMENT -> "새 댓글";
			case REPLY -> "새 답글";
			case CHAT -> "새 메시지";
			case POINT -> "포인트 적립";
			case PROBLEM_FORM -> "문제 양식";
			case RANK -> "순위 변경";
			case TIL -> "TIL 알림";
			case SUBMISSION_FORM -> "제출 양식";
			case JOIN_REQUEST -> "가입 요청";
			case REACTION -> "반응";
			case STUDY_APPLICATION -> "스터디 신청";
			case LIKE -> "좋아요";
			case NOTICE -> "공지사항";
			case SOLVE -> "문제 해결";
			case STUDY_APPROVE -> "스터디 승인";
			case CHOOSED_COMMENT -> "채택된 댓글";
			case CLUVER -> "클러버 알림";
			default -> "알림";
		};
	}
}
