package com.example.cluvrnotifications.domain.user.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrnotifications.domain.user.dto.request.LoginUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.request.SignUpUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.response.LoginUserResponseDto;
import com.example.cluvrnotifications.domain.user.dto.response.SignUpUserResponseDto;
import com.example.cluvrnotifications.domain.user.service.UserService;
import com.example.cluvrnotifications.global.response.BaseResponse;
import com.example.cluvrnotifications.global.response.ResponseCode;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<BaseResponse<SignUpUserResponseDto>> signUp(
		@Valid @RequestBody SignUpUserRequestDto signUpUserRequestDto
	) {
		SignUpUserResponseDto responseDto = userService.signUp(signUpUserRequestDto);
		return ResponseEntity.ok(BaseResponse.success(responseDto, ResponseCode.CREATED));
	}

	@PostMapping("/login")
	public ResponseEntity<BaseResponse<LoginUserResponseDto>> login(
		@Valid @RequestBody LoginUserRequestDto loginUserRequestDto
	) {
		LoginUserResponseDto responseDto = userService.login(loginUserRequestDto);
		return ResponseEntity.ok(BaseResponse.success(responseDto, ResponseCode.OK));
	}

}
