package com.example.cluvrnotifications.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;

/**
 * 설명:  NotificationEvent를 수신한 후, SSE 연결 여부에 따라 알림을 전송하거나, MongoDB에 임시 저장하는 역할을 수행하는 서비스
 *
 * - 연결됨(if문): SseEmitter를 통해 알림 실시간 전송
 * - 미연결(else문): NotificationDocument로 변환하여 MongoDB에 저장
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

		boolean sent = notificationSendService.send(event);

		if (!sent) {
			NotificationDocument doc = new NotificationDocument(
				event.getReceiverId(),
				event.getType(),
				event.getContent(),
				event.getTargetType(),
				event.getTargetId()
			);
			notificationCacheRepository.save(doc);
			log.debug(" SSE 끊김 -> 알림 저장됨: 받는 사람: {}, 내용 : {}  ", event.getReceiverId(), event.getContent());

		} else {
			log.debug(" SSE 연결됨 -> 알림 전송 완료: 받을 사람: {}, 내용 : {}  ", event.getReceiverId(), event.getContent());
		}

	}
}
