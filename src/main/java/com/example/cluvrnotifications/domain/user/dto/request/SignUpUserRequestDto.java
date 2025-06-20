package com.example.cluvrnotifications.domain.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.cluvrnotifications.domain.user.entity.enums.CategoryDetail;
import com.example.cluvrnotifications.domain.user.entity.enums.Gender;

@Getter
@RequiredArgsConstructor
public class SignUpUserRequestDto {
	@NotBlank(message = "이름을 입력해야 합니다.")
	@Size(max = 10, message = "이름은 최대 10자까지 가능합니다.")
	private final String name;

	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	private final LocalDate birthday;

	@NotBlank(message = "이메일을 입력해야 합니다.")
	@Email(message = "유효한 이메일 형식이어야 합니다.")
	private final String email;

	@NotBlank(message = "전화번호를 입력해야 합니다.")
	@Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자 10~11자리여야 합니다.")
	private final String phoneNumber;

	@NotNull(message = "성별을 선택해야 합니다.")
	private final Gender gender;

	@NotNull(message = "카테고리(세부)를 선택해야 합니다.")
	private final CategoryDetail categoryDetail;

	private final String imageUrl;

	@NotBlank(message = "비밀번호를 입력해야 합니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.")
	private final String password;

	@NotBlank(message = "비밀번호 확인을 입력해야 합니다.")
	private final String confirmPassword;
}
