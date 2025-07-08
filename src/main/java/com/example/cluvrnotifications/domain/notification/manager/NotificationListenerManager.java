package com.example.cluvrnotifications.domain.notification.manager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import com.example.cluvrnotifications.domain.notification.dto.event.NotificationEvent;
import com.example.cluvrnotifications.domain.notification.service.NotificationReceiveService;
import com.example.cluvrnotifications.global.exception.BusinessException;
import com.example.cluvrnotifications.global.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 설명: 사용자별 큐(queue.user.{id})를 동적으로 생성하고 바인딩하는 서비스
 *
 * @author escomputer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationListenerManager {

	private final AmqpAdmin amqpAdmin;
	private final DirectExchange notificationExchange;
	private final ConnectionFactory connectionFactory;
	private final ObjectMapper objectMapper;
	private final NotificationReceiveService notificationReceiveService;

	private final Map<Long, SimpleMessageListenerContainer> listenerContainers = new ConcurrentHashMap<>();

	/**
	 * 설명: 사용자 ID 기준으로 큐를 동적으로 생성하고, exchange에 바인딩
	 */
	public void start(Long userId) {
		if (listenerContainers.containsKey(userId)) {
			log.debug("이미 존재하는 컨테이너 있음: user.{}", userId);
			return;
		}

		String queueName = "user." + userId;

		SimpleMessageListenerContainer container = listenerContainers.computeIfAbsent(userId, id -> {
			createQueueAndBinding(queueName);
			SimpleMessageListenerContainer c = createContainer(queueName);
			c.start();
			log.debug("user.{} 컨테이너 생성 및 시작", userId);
			return c;
		});

		if (container != null && container.isRunning()) {
			log.debug("user.{} 컨테이너 이미 실행 중", userId);
		}
	}

	public void stop(Long userId) {
		SimpleMessageListenerContainer container = listenerContainers.remove(userId);
		if (container != null) {
			container.stop();
			container.destroy();
			log.debug("user.{} 컨테이너 중단", userId);
		}
	}

	/**
	 * 설명: 큐를 동적으로 생성하고 DLX(DLQ) 바인딩까지 함께 처리합니다.
	 */
	private void createQueueAndBinding(String queueName) {
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "dead.exchange");
		args.put("x-dead-letter-routing-key", "dead.queue");

		Queue queue = new Queue(queueName, true, false, false, args);
		amqpAdmin.declareQueue(queue);

		Binding binding = BindingBuilder
			.bind(queue)
			.to(notificationExchange)
			.with(queueName);

		amqpAdmin.declareBinding(binding);
	}

	/**
	 * 설명: 주어진 큐 이름에 대해 리스너 컨테이너를 생성합니다.
	 */
	private SimpleMessageListenerContainer createContainer(String queueName) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);

		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener((ChannelAwareMessageListener)(message, channel) -> {
			Long tag = message.getMessageProperties().getDeliveryTag();

			if (channel == null) {
				log.error("채널이 null이라 ACK/NACK을 실행할 수 없음. 메시지 유실 위험!");
				throw new BusinessException(ResponseCode.CHANNEL_NULL);
			}

			try {
				String raw = new String(message.getBody(), StandardCharsets.UTF_8);
				log.debug("RAW 메시지: {} ", raw);
				NotificationEvent event = objectMapper.readValue(raw, NotificationEvent.class);
				log.debug("{} 알림 수신 : {} ", queueName, event.getContent());
				notificationReceiveService.receive(event);
				channel.basicAck(tag, false);
			} catch (Exception e) {
				log.error("메시지 파싱 실패", e);

				int retryCount = getRetryCount(message);

				if (retryCount >= 3) {
					log.warn("최대 재시도 횟수 초과로 인해 DLQ로 전송합니다.");
					channel.basicReject(tag, false);
				} else {
					channel.basicNack(tag, false, true);
				}
			}
		});

		return container;
	}

	/**
	 * 설명: 메시지의 헤더에서 x-death 정보를 파싱하여 현재까지 실패 횟수를 반환합니다.
	 */
	private int getRetryCount(Message message) {
		List<Map<String, ?>> xDeathHeader = (List<Map<String, ?>>)
			message.getMessageProperties().getHeaders().get("x-death");
		if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
			Map<String, ?> death = xDeathHeader.get(0);
			Object count = death.get("count");
			if (count instanceof Long) {
				return ((Long)count).intValue();
			}
		}
		return 0;
	}


	public void createQueueIfNotExists(Long userId) {
		String queueName = "user." + userId;

		Properties props = amqpAdmin.getQueueProperties(queueName);
		if (props != null) {
			log.debug("이미 존재하는 큐: {}", queueName);
			return;
		}

		createQueueAndBinding(queueName);
		log.info("큐 생성 완료: {}", queueName);
	}
}
