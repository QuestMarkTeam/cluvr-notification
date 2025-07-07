package com.example.cluvrnotifications.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


	private final JwtDecoder jwtDecoder;

	public SecurityConfig(
		JwtDecoder jwtDecoder
	) {
		this.jwtDecoder = jwtDecoder;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration configuration
	) throws Exception {
		return configuration.getAuthenticationManager();
	}


	@Bean
	public SecurityFilterChain defaultChain(HttpSecurity http) throws
		Exception {

		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.securityMatcher("/api/**", "/notifications/**")
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
			.authorizeHttpRequests(auth -> auth
				// 회원가입·로그인만 공개
				.requestMatchers("api/auth/**", "/my-monitor/**").permitAll()
				.requestMatchers("/notifications/stream/connect").permitAll()
				// /admin/** 은 ADMIN 권한 필요
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 그 외 모든 요청은 인증된 사용자여야 함
				.anyRequest().authenticated()
			).oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.decoder(jwtDecoder))
			);


		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 메인 도메인 추가
		configuration.setAllowedOrigins(List.of(
			"https://cluvr.co.kr",
			"https://www.cluvr.co.kr"  // www 서브도메인도 추가
		));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);


		configuration.setExposedHeaders(List.of("*"));


		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
