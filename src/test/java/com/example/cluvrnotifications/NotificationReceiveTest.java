package com.example.cluvrnotifications;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import com.example.cluvrnotifications.domain.notification.manager.NotificationListenerManager;
import com.example.cluvrnotifications.domain.notification.repository.base.NotificationCacheRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class NotificationReceiveTest {

	@Autowired
	private RabbitTemplate testRabbitTemplate;

	@Autowired
	private NotificationListenerManager listenerManager;
	@Autowired
	private NotificationCacheRepository notificationCacheRepository;

	@Test
	void MQ_메시지를_Notification_모듈이_정상_수신한다() throws InterruptedException {
		// given
		Long userId = 9999L;

		// 핵심: 리스너 시작 → MQ에 이미 들어있는 메시지를 소비함
		listenerManager.start(userId);

		// 수신 대기 (리스너가 컨테이너 생성 + consume할 시간)
		Thread.sleep(2000);

	}

	@TestConfiguration
	static class RabbitTestConfig {

		@Bean
		public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
			RabbitTemplate template = new RabbitTemplate(connectionFactory);
			template.setMessageConverter(new Jackson2JsonMessageConverter());
			return template;
		}
	}

}
