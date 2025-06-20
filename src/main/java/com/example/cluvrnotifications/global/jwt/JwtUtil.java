package com.example.cluvrnotifications.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	public final long ACCESS_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 2;
	public final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7;
	@Value("${jwt.secret.key}")
	private String secretKey;

	public String generateAccessToken(Long userId, String role) {
		return generateToken(userId, role, ACCESS_TOKEN_EXPIRATION_MS);
	}

	public String generateRefreshToken(Long userId, String role) {
		return generateToken(userId, role, REFRESH_TOKEN_EXPIRATION_MS);
	}

	private String generateToken(Long userId, String role, long expirationMillis) {

		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		Key key = Keys.hmacShaKeyFor(keyBytes);

		Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
		claims.put("role", role);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMillis);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Long getUserIdFromToken(String token) {
		return Long.valueOf(parseToken(token).getSubject());
	}

	public String getUserRoleFromToken(String token) {
		return parseToken(token).get("role", String.class);
	}

	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	private Claims parseToken(String token) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		Key key = Keys.hmacShaKeyFor(keyBytes);

		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
}
