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
 * - SSE 연결 시 save()
 * - 알림 전송 시 get()
 * - 연결 끊길 경우 delete()
 *
 *
 * @author escomputer
 */

@Repository
@Slf4j
public class SseEmitterRepository {

	private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

	/**
	 * 설명: 사용자 ID 기준으로 emitter를 저장
	 *
	 * <p>userId 키에 해당하는 emitter 리스트가 없으면 새 리스트 생성, 그 후 emitter 추가
	 *
	 * @param userId 사용자 ID
	 * @param emitter 연결된 SseEmitter
	 *
	 * @author escomputer
	 */

	public void save(Long userId, SseEmitter emitter) {
		emitterMap.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);
		log.info("Emitter 저장 완료: userId={}, 총 연결 수={}", userId, emitterMap.get(userId).size());
	}

	/**
	 * 설명: 사용자 ID 기준으로 emitter를 조회
	 *
	 * <p>유저의 모든 emitter 반환
	 *
	 * @param userId 사용자 ID
	 * @return 해당 사용자의 emitter 또는 null
	 *
	 * @author escomputer
	 */
	public List<SseEmitter> get(Long userId) {
		return emitterMap.getOrDefault(userId, List.of());
	}

	/**
	 * 설명: 사용자 ID 기준으로 특정 emitter를 삭제
	 *
	 * @param userId 사용자 ID
	 *
	 * @author escomputer
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
	 *
	 * 나중에 고도화때 쓸 메서드입니다. (로그아웃, 탈퇴시에)
	 *
	 * @author escomputer
	 */

	public void deleteAll(Long userId) {
		emitterMap.remove(userId);
		log.debug("Emitter 전체 삭제: userId={}", userId);
	}
}
