package com.example.cluvrnotifications.domain.notification.controller;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cluvrnotifications.common.annotation.Auth;
import com.example.cluvrnotifications.common.dto.AuthUser;
import com.example.cluvrnotifications.common.dto.PageResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationsSettingResponseDto;
import com.example.cluvrnotifications.domain.notification.enums.NotificationType;
import com.example.cluvrnotifications.domain.notification.service.NotificationService;
import com.example.cluvrnotifications.global.response.BaseResponse;
import com.example.cluvrnotifications.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<BaseResponse<PageResponseDto<ReadNotificationResponseDto>>> getNotifications(

		@Auth AuthUser authUser,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) Boolean isRead
	) {
		PageResponseDto<ReadNotificationResponseDto> result = notificationService.getNotifications(authUser.id(), page,
			size,
			isRead);
		return ResponseEntity.ok(BaseResponse.success(result, ResponseCode.NOTI_FETCH_SUCCESS));
	}

	@PatchMapping("/{id}/read")
	public ResponseEntity<BaseResponse<Void>> readNotification(
		@Auth AuthUser authUser,
		@PathVariable Long id
	) {
		notificationService.markAsRead(authUser.id(), id);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_READ_SUCCESS));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> deleteNotification(
		@Auth AuthUser authUser,
		@PathVariable Long id
	) {
		notificationService.deleteNotification(authUser.id(), id);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_DELETE_SUCCESS));
	}

	@PatchMapping("/settings")
	public ResponseEntity<BaseResponse<Void>> updateSettings(
		@Auth AuthUser authUser,
		@RequestBody Map<NotificationType, Boolean> settings
	) {
		notificationService.updateSettings(authUser.id(),
			settings);
		return ResponseEntity.ok(BaseResponse.success(ResponseCode.NOTI_UPDATE_SUCCESS));
	}

	@GetMapping("/settings")
	public ResponseEntity<BaseResponse<ReadNotificationsSettingResponseDto>> getSettings(
		@Auth AuthUser authUser
	) {
		ReadNotificationsSettingResponseDto dto = notificationService.getSettings(authUser.id());
		return ResponseEntity.ok(BaseResponse.success(dto, ResponseCode.NOTI_FETCH_SUCCESS));
	}

	@GetMapping("/notifications/{id}")
	public ResponseEntity<BaseResponse<ReadNotificationResponseDto>> getNotification(
		@Auth AuthUser authUser,
		@PathVariable Long id
	) {
		ReadNotificationResponseDto dto = notificationService.getNotification(authUser.id(), id);
		return ResponseEntity.ok(BaseResponse.success(dto, ResponseCode.NOTI_FETCH_SUCCESS));
	}

}
