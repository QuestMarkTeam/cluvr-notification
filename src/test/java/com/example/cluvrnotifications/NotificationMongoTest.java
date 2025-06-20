package com.example.cluvrnotifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.entity.NotificationDocument;
import com.example.cluvrnotifications.domain.notification.enums.NotiTargetType;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;
import com.example.cluvrnotifications.domain.notification.service.NotificationReceiveService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class NotificationMongoTest {

	@Autowired
	private NotificationCacheRepository notificationRepository;

	@Autowired
	private NotificationReceiveService notificationReceiveService;

	@Test
	void MQ_알림_이벤트가_MongoDB에_정상_저장된다() {
		// given
		NotificationEvent event = NotificationEvent.from(
			9999L,
			NotificationType.COMMENT,
			"!!!!!!!!Mongo 저장 테스트 메시지",
			NotiTargetType.USER,
			123L
		);

		// when
		notificationReceiveService.receive(event);

		// then
		List<NotificationDocument> list = notificationRepository.findByReceiverId(9999L);
		assertFalse(list.isEmpty());

		NotificationDocument saved = list.get(0);
		assertEquals("!!!!!!!!Mongo 저장 테스트 메시지", saved.getContent());
		System.out.println("!!!!!!!!!!저장된 알림: " + saved.getNotificationType() + " / " + saved.getContent());
	}
}
