package com.example.cluvrnotifications.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebClientConfig implements WebMvcConfigurer {

	@Value("https://api.cluvr.co.kr")
	private String apiServerBaseUrl;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(apiServerBaseUrl)
			.filter((request, next) -> {
				// 요청 로그 추가
				System.out.println("Request: " + request.method() + " " + request.url());
				return next.exchange(request);
			})
			.build();
	}
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("https://cluvr.co.kr")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
			.allowCredentials(true);
	}

}
