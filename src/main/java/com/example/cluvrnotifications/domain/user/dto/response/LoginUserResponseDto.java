package com.example.cluvrnotifications.domain.user.dto.response;

import lombok.Getter;

import com.example.cluvrnotifications.domain.user.entity.User;

@Getter
public class LoginUserResponseDto {
	private final Long id;
	private final String name;
	private final String email;
	private final String accessToken;
	private final String refreshToken;

	public LoginUserResponseDto(Long id, String name, String email, String accessToken, String refreshToken) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	/**
	 * User 엔티티와 생성된 토큰 두 개를 받아서 DTO를 생성합니다.
	 */
	public static LoginUserResponseDto from(User user, String accessToken, String refreshToken) {
		return new LoginUserResponseDto(user.getId(), user.getName(), user.getEmail(), accessToken, refreshToken);
	}
}
