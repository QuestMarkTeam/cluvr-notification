package com.example.cluvrnotifications.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("localhost://8080")
	private String apiServerBaseUrl;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(apiServerBaseUrl)
			.build();
	}
}