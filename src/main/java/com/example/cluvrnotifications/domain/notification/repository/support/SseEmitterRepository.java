package com.example.cluvrnotifications.domain.notification.repository.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 설명: 사용자별 SseEmitter를 메모리에 저장하고 관리하는 저장소 클래스
 *
 * @author escomputer
 */
@Repository
@Slf4j
public class SseEmitterRepository {

	private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

	/**
	 * 설명: 사용자 ID 기준으로 emitter를 저장
	 */
	public void save(Long userId, SseEmitter emitter) {
		emitterMap.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);
		log.info("Emitter 저장 완료: userId={}, 총 연결 수={}", userId, emitterMap.get(userId).size());
	}

	/**
	 * 설명: 사용자 ID 기준으로 emitter를 조회
	 */
	public List<SseEmitter> get(Long userId) {
		return emitterMap.getOrDefault(userId, List.of());
	}

	/**
	 * 설명: 사용자 ID 기준으로 특정 emitter를 삭제
	 */
	public void delete(Long userId, SseEmitter emitter) {
		List<SseEmitter> emitters = emitterMap.get(userId);
		if (emitters != null) {
			emitters.remove(emitter);
			log.debug("Emitter 삭제: userId={}, 남은 연결 수={}", userId, emitters.size());
			if (emitters.isEmpty()) {
				emitterMap.remove(userId);
			}
		}
	}

	/**
	 * 설명: 유저의 모든 emitter삭제
	 */
	public void deleteAll(Long userId) {
		emitterMap.remove(userId);
		log.debug("Emitter 전체 삭제: userId={}", userId);
	}
}
