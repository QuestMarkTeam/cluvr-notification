package com.example.cluvrnotifications.domain.notification.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.manager.NotificationListenerManager;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;
import com.example.cluvrnotifications.domain.notification.repository.support.SseEmitterRepository;

/**
 * 설명: SSE연결 처리 + MongoDB에서 알림 꺼내서 전송하는 서비스
 *
 * @author escomputer
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationStreamService {

	private static final Long TIMEOUT = 60 * 1000L; //60초 타임아웃

	private final SseEmitterRepository sseEmitterRepository;
	private final NotificationCacheRepository notificationCacheRepository;
	private final NotificationListenerManager notificationListenerManager;

	/**
	 * 설명: SSE 연결을 생성하고, 기존 MongoDB의 읽지 않은 알림을 전송 후 읽음 처리
	 *
	 * @param userId 사용자 ID
	 * @return SseEmitter 객체
	 *
	 * @author escomputer
	 */
	@Transactional
	public SseEmitter connect(Long userId) {
		// 로그인 시점에 큐를 자동으로 생성하고 바인딩
		notificationListenerManager.start(userId);

		SseEmitter emitter = new SseEmitter(TIMEOUT);
		sseEmitterRepository.save(userId, emitter);

		Runnable cleanUp = () -> {
			log.debug("SSE 연결 종료 -> 리스너 중단 및 Emitter 제거: user.{}", userId);
			notificationListenerManager.stop(userId);
			sseEmitterRepository.delete(userId, emitter);
			emitter.complete();
		};

		// SSE 연결 종료 시(+ 타임아웃)에 자동으로 컨테이너도 중단됨
		emitter.onCompletion(cleanUp);
		emitter.onTimeout(cleanUp);
		emitter.onError(ex -> {
			log.warn("SSE 오류 발생: 사용자 ID = {}, 오류 = {}", userId, ex.toString());
			cleanUp.run();
		});

		// MongoDB에서 읽지 않은 알림 조회
		List<NotificationDocument> unreadNotifications = notificationCacheRepository.findByReceiverIdAndIsReadFalse(userId);
		log.debug("MongoDB에서 읽지 않은 알림 개수: {}", unreadNotifications.size());

		// 전송 실패한 알림 목록
		List<NotificationDocument> failedToSend = new ArrayList<>();

		// 읽지 않은 알림들을 SSE로 전송
		unreadNotifications.forEach(doc -> {
			try {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(NotificationEvent.from(
						doc.getReceiverId(),
						doc.getNotificationType(),
						doc.getContent(),
						doc.getTargetType(),
						doc.getTargetId()
					)));

				log.debug("SSE로 전송할 알림: {}", doc.getContent());

			} catch (IOException e) {
				log.warn("알림 전송 실패 -> 읽지 않음 상태 유지: {}", doc.getContent());
				failedToSend.add(doc);
				sseEmitterRepository.delete(userId, emitter);
			}
		});

		// 전송 성공한 알림들만 읽음 처리
		List<NotificationDocument> successfulSent = new ArrayList<>(unreadNotifications);
		successfulSent.removeAll(failedToSend);
		
		successfulSent.forEach(doc -> {
			doc.markAsRead();
		});
		
		if (!successfulSent.isEmpty()) {
			notificationCacheRepository.saveAll(successfulSent);
			log.debug("전송 완료한 알림 {}개를 읽음 처리했습니다.", successfulSent.size());
		}

		return emitter;
	}

}
