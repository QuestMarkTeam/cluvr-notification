package com.example.cluvrnotifications.domain.notification.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public class ReadNotificationResponseDto {
	private Long id;
	private String content;
	private Boolean isRead;

	@QueryProjection
	public ReadNotificationResponseDto(Long id, String content, Boolean isRead) {
		this.id = id;
		this.content = content;
		this.isRead = isRead;
	}
}
