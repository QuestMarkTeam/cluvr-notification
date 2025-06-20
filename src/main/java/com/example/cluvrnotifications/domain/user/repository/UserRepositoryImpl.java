package com.example.cluvrnotifications.domain.user.repository;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.example.cluvrnotifications.domain.user.entity.QUser;
import com.example.cluvrnotifications.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<User> findByEmail(String email) {
		QUser u = QUser.user;
		User user = queryFactory
			.selectFrom(u)
			.where(u.email.eq(email))
			.fetchOne();

		return Optional.ofNullable(user);
	}
}
