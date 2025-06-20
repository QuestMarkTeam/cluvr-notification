package com.example.cluvrnotifications.global.jwt;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
	private static final String REDIS_KEY_PREFIX = "refreshToken:";
	private final JwtUtil jwtUtil;
	private final StringRedisTemplate redisTemplate;

	@Override
	public String createRefreshToken(Long userId, String role) {
		String refreshToken = jwtUtil.generateRefreshToken(userId, role);

		String redisKey = REDIS_KEY_PREFIX + userId;

		redisTemplate.opsForValue()
			.set(redisKey, refreshToken, jwtUtil.REFRESH_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);

		return refreshToken;
	}

	@Override
	public boolean validateRefreshToken(String token) {
		if (!jwtUtil.validateToken(token)) {
			return false;
		}

		Long userId = jwtUtil.getUserIdFromToken(token);

		String redisKey = REDIS_KEY_PREFIX + userId;
		String storedToken = redisTemplate.opsForValue().get(redisKey);

		return token.equals(storedToken);
	}

	@Override
	public void deleteRefreshToken(Long userId) {
		String redisKey = REDIS_KEY_PREFIX + userId;
		redisTemplate.delete(redisKey);
	}
}
