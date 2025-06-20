package com.example.cluvrnotifications.domain.user.service;

import com.example.cluvrnotifications.domain.user.dto.request.LoginUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.request.SignUpUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.response.LoginUserResponseDto;
import com.example.cluvrnotifications.domain.user.dto.response.SignUpUserResponseDto;

public interface UserService {
	SignUpUserResponseDto signUp(SignUpUserRequestDto requestDto);

	LoginUserResponseDto login(LoginUserRequestDto requestDto);
}
