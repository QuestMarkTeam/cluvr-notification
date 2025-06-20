package com.example.cluvrnotifications.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class LoginUserRequestDto {
	@NotBlank(message = "이메일을 입력해야 합니다.")
	@Email(message = "유효한 이메일 형식이어야 합니다.")
	private String email;

	@NotBlank(message = "비밀번호를 입력해야 합니다.")
	private String password;
}
