package com.example.cluvrnotifications.domain.user.dto.response;

import lombok.Getter;

import com.example.cluvrnotifications.domain.user.entity.User;

@Getter
public class SignUpUserResponseDto {
	private Long id;
	private String name;
	private String email;

	public SignUpUserResponseDto(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public static SignUpUserResponseDto from(User user) {
		return new SignUpUserResponseDto(
			user.getId(),
			user.getName(),
			user.getEmail()
		);
	}
}
