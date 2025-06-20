package com.example.cluvrnotifications.global.jwt;

public interface RefreshTokenService {

	String createRefreshToken(Long userId, String role);

	boolean validateRefreshToken(String token);

	void deleteRefreshToken(Long userId);
}
