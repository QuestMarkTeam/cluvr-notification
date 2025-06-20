package com.example.cluvrnotifications.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitDLQConfig {

	@Bean
	public DirectExchange deadExchange() {
		return new DirectExchange("dead.exchange");
	}

	@Bean
	public Queue deadQueue() {
		return new Queue("dead.queue", true);
	}

	@Bean
	public Binding deadBinding() {
		return BindingBuilder.bind(deadQueue())
			.to(deadExchange())
			.with("dead.queue");
	}
}
