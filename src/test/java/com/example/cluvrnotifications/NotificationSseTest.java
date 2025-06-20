package com.example.cluvrnotifications;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.example.cluvrnotifications.global.config.TestSecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class NotificationSseTest {

	@Test
	void SSE_연결_정상_확인() throws Exception {
		// given
		String url = "http://localhost:8080/api/notifications/subscribe?userId=9999";
		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "text/event-stream");

		// when
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = reader.readLine();

		// then
		assertThat(connection.getResponseCode()).isEqualTo(200);
		System.out.println("!!! SSE 연결 성공, 첫 응답: " + line);
	}
}
