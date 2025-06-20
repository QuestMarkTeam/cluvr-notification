package com.example.cluvrnotifications.domain.user.repository;

import java.util.Optional;

import com.example.cluvrnotifications.domain.user.entity.User;

public interface UserRepositoryCustom {
	Optional<User> findByEmail(String email);
}
