package com.example.cluvrnotifications.domain.notification.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.repository.support.SseEmitterRepository;

/**
 * 설명: SseEmitterRepository를 통해 연결된 유저의 SSEEmitter를 조회하고,
 * 실시간으로 알림을 전송하는 서비스
 *
 * 전송 중 예외가 발생하면 연결을 종료하고 emitter를 제거
 *
 * @author escomputer
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSendService {

	private final SseEmitterRepository sseEmitterRepository;

	/**
	 * 설명: 유저의 SSEEmitter가 존재하면 알림을 전송
	 *
	 *
	 * @param  event 알림 이벤트 객체
	 *
	 * @return 전송 성공 여부 (true = 전송됨, false = 실패하여 저장 필요)
	 * @author escomputer
	 */

	public boolean send(NotificationEvent event) {
		List<SseEmitter> emitters = sseEmitterRepository.get(event.getReceiverId());

		if (emitters.isEmpty()) {
			return false;
		}

		boolean success = false;

		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(event));
				success = true;
				log.debug("전송 대상 emitter 수: {}", emitters.size());
			} catch (Exception e) {
				log.warn("SSE 전송 실패 - 사용자 Id :{}, 사유 : {}, 내용 : {}", event.getReceiverId(), e, e.getMessage());
				sseEmitterRepository.delete(event.getReceiverId(), emitter);
			}
		}

		return success;
	}
}
