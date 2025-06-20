package com.example.cluvrnotifications.domain.notification.repository.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.cluvrnotifications.domain.notification.dto.response.QReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.dto.response.ReadNotificationResponseDto;
import com.example.cluvrnotifications.domain.notification.entity.Notification;
import com.example.cluvrnotifications.domain.notification.entity.NotificationSetting;
import com.example.cluvrnotifications.domain.notification.entity.QNotification;
import com.example.cluvrnotifications.domain.notification.entity.QNotificationSetting;
import com.example.cluvrnotifications.domain.notification.repository.custom.NotificationRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ReadNotificationResponseDto> findAllDtosByUserId(Long userId, Boolean isRead, Pageable pageable) {
		QNotification notification = QNotification.notification;

		List<ReadNotificationResponseDto> content = queryFactory
			.select(new QReadNotificationResponseDto(
				notification.id,
				notification.content,
				notification.isRead
			))
			.from(notification)
			.where(
				notification.user.id.eq(userId),
				isRead != null ? notification.isRead.eq(isRead) : null
			)
			.orderBy(notification.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(notification.count())
			.from(notification)
			.where(
				notification.user.id.eq(userId),
				isRead != null ? notification.isRead.eq(isRead) : null
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, count != null ? count : 0);
	}

	@Override
	public Optional<Notification> findByIdAndUserId(Long id, Long userId) {
		QNotification notification = QNotification.notification;

		Notification result = queryFactory
			.selectFrom(notification)
			.where(
				notification.id.eq(id),
				notification.user.id.eq(userId)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<NotificationSetting> findAllByUserId(Long userId) {
		QNotificationSetting setting = QNotificationSetting.notificationSetting;

		return queryFactory
			.selectFrom(setting)
			.where(setting.user.id.eq(userId))
			.fetch();
	}
}
