package com.example.cluvrnotifications.domain.notification.manager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 *
	 * 다른 스레드가 들어와서 같은 userId로 또 컨테이너 만들 수 있음을 방지하여
	 * computeIfAbsent
	 *
	 * @param userId 사용자 ID
	 *
	 * @author escomputer
	 */

	public void start(Long userId) {

		//중복생성방지
		if (listenerContainers.containsKey(userId)) {
			log.debug("이미 존재하는 컨테이너 있음: user.{}", userId);
			return;
		}

		String queueName = "user." + userId;

		//리스너 컨테이너 생성 (동적으로 생성되는 큐를 리스닝함)
		SimpleMessageListenerContainer container = listenerContainers.computeIfAbsent(userId, id -> {
			// 큐 생성 & 바인딩
			createQueueAndBinding(queueName);
			SimpleMessageListenerContainer c = createContainer(queueName);
			c.start();
			log.debug("user.{} 컨테이너 생성 및 시작", userId);
			return c;    //여기서 put까지 일어나서 메모리에 저장이됨.

		});

		// 이미 존재했을 경우에도 log를 남기고 끝낼 수 있음
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
	 * 설명: 큐 삭제
	 *
	 * <p>만약에 사용자가 탈퇴하면 미사용 큐가 되므로 자원낭비 방지를 위하여
	 * 이건 고도화때 사용할 메서드입니다.
	 *
	 * @author escomputer
	 */

	public void remove(Long userId) {

		//먼저 컨테이너를 중단 후 삭제 (ChannelShutDownException 방지)
		stop(userId);

		String queueName = "user." + userId;
		amqpAdmin.deleteQueue(queueName);
		log.debug("user.{} 큐 삭제 완료", userId);
	}

	/**
	 * 설명: 큐를 동적으로 생성하고 DLX(DLQ) 바인딩까지 함께 처리합니다.
	 *
	 * <p>사용자 접속 시 호출되며, 해당 사용자의 전용 큐(queue.user.{id})가 없으면 생성합니다.
	 * DLX(dead.exchange), DLQ(dead.queue)와도 함께 바인딩되어야 하므로 arguments를 포함합니다.
	 * 큐에 메시지는 살아있어야한다(접속 종료, 재시작 등등에도)
	 * 그래서 durable !!
	 *
	 * @param queueName 생성할 큐의 이름 (예: user.123)
	 * @author escomputer
	 */
	private void createQueueAndBinding(String queueName) {
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "dead.exchange");        // DLX 이름
		args.put("x-dead-letter-routing-key", "dead.queue");        // DLQ 큐 이름 (routing key로 사용됨)

		//durable = true ,exclusive = false , autoDelet=false
		Queue queue = new Queue(queueName, true, false, false, args);
		amqpAdmin.declareQueue(queue);

		Binding binding = BindingBuilder
			.bind(queue)
			.to(notificationExchange)
			.with(queueName);//queuename = 라우팅 키

		amqpAdmin.declareBinding(binding);
	}

	/**
	 * 설명: 주어진 큐 이름에 대해 리스너 컨테이너를 생성합니다.
	 *
	 * <p>해당 컨테이너는 MANUAL ACK 모드로 동작하며, 메시지 처리 실패 시
	 * 최대 3회 재시도 후 DLQ(dead.queue)로 전송됩니다.
	 *
	 * @param queueName 수신할 큐 이름 (예: user.123)
	 * @return 생성된 SimpleMessageListenerContainer 인스턴스
	 * @throws BusinessException 채널이 null인 경우 (중대 오류)
	 * @author escomputer
	 */
	private SimpleMessageListenerContainer createContainer(String queueName) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);

		//ack 모드(정보전달모드)
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener((ChannelAwareMessageListener)(message, channel) -> {
			Long tag = message.getMessageProperties().getDeliveryTag();

			if (channel == null) {
				log.error("채널이 null이라 ACK/NACK을 실행할 수 없음. 메시지 유실 위험!");
				throw new BusinessException(ResponseCode.CHANNEL_NULL);
			}

			try {
				String raw = new String(message.getBody(), StandardCharsets.UTF_8);
				log.debug("!!!!!!!!!!!!!!!! RAW 메시지: {} ", raw);
				NotificationEvent event = objectMapper.readValue(raw, NotificationEvent.class);
				log.debug("{} 알림 수신 : {} ", queueName, event.getContent());
				notificationReceiveService.receive(event);
				channel.basicAck(tag, false);
			} catch (Exception e) {
				log.error("메시지 파싱 실패", e);

				int retryCount = getRetryCount(message);

				if (retryCount >= 3) {
					log.warn("최대 재시도 횟수 초과로 인해 DLQ로 전송합니다.");
					channel.basicReject(tag, false);    //DLQ
				} else {
					channel.basicNack(tag, false, true);
				}
			}
		});

		return container;
	}

	/**
	 * 설명: 메시지의 헤더에서 x-death 정보를 파싱하여 현재까지 실패 횟수를 반환합니다.
	 *
	 * <p>RabbitMQ는 DLQ로 전송되었던 메시지에 대해 x-death 헤더를 자동으로 부여합니다.
	 * 이를 기반으로 몇 번 재시도되었는지 파악할 수 있습니다.
	 *
	 * @param message RabbitMQ 메시지
	 * @return 재시도 횟수 (없으면 0)
	 * @author escomputer
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
}
