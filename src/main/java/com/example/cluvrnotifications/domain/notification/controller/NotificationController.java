package com.example.cluvrnotifications.domain.notification.controller;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrnotifications.common.dto.PageResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationsSettingResponseDto;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.notification.service.NotificationService;
import com.example.cluvrnotifications.global.response.BaseResponse;
import com.example.cluvrnotifications.global.response.ResponseCode;
import com.example.cluvrnotifications.global.util.JwtUserExtractor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

	private final NotificationService notificationService;
	private final JwtUserExtractor jwtUserExtractor;

	@GetMapping
	public ResponseEntity<BaseResponse<PageResponseDto<ReadNotificationResponseDto>>> getNotifications(
		@AuthenticationPrincipal Jwt jwt,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Boolean isRead
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		log.info("이ㅏㅁ널이ㅏㅁ너ㅗ라;ㅣㄴㅇ머라;ㅣㅁㄴ어라ㅣ;ㅁㄴ어ㅏ;리ㅓㄴㅇ마;ㅐㅣ");
		PageResponseDto<ReadNotificationResponseDto> result = notificationService.getNotifications(userId, page,
			size, isRead);
		return ResponseEntity.ok(BaseResponse.success(result, ResponseCode.NOTI_FETCH_SUCCESS));
	}

	@PatchMapping("/{id}/read")
	public ResponseEntity<BaseResponse<Void>> readNotification(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String id  // Long → String 변경
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		notificationService.markAsRead(userId, id);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_READ_SUCCESS));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> deleteNotification(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String id  // Long → String 변경
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		notificationService.deleteNotification(userId, id);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_DELETE_SUCCESS));
	}

	@PatchMapping("/settings")
	public ResponseEntity<BaseResponse<Void>> updateSettings(
		@AuthenticationPrincipal Jwt jwt,
		@RequestBody Map<NotificationType, Boolean> settings
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		notificationService.updateSettings(userId, settings);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_UPDATE_SUCCESS));
	}

	@GetMapping("/settings")
	public ResponseEntity<BaseResponse<ReadNotificationsSettingResponseDto>> getSettings(
		@AuthenticationPrincipal Jwt jwt
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		ReadNotificationsSettingResponseDto dto = notificationService.getSettings(userId);
		return ResponseEntity.ok(BaseResponse.success(dto, ResponseCode.NOTI_FETCH_SUCCESS));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ReadNotificationResponseDto>> getNotification(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String id  // Long → String 변경
	) {
		Long userId = jwtUserExtractor.extractUserId(jwt);
		ReadNotificationResponseDto dto = notificationService.getNotification(userId, id);
		return ResponseEntity.ok(BaseResponse.success(dto, ResponseCode.NOTI_FETCH_SUCCESS));
	}
}
