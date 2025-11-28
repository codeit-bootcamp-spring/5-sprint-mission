package com.sprint.mission.discodeit.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final DiscodeitUserDetailsService userDetailsService;

	@Value("${security.jwt.secret}")
	private String secretKey;

	@Value("${security.jwt.access-token-validity-seconds}")
	private long accessTokenValiditySeconds;

	@Value("${security.jwt.refresh-token-validity-seconds}")
	private long refreshTokenValiditySeconds;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Access Token 생성
	 */
	public String createAccessToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenValiditySeconds * 1000);

		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key)
			.compact();
	}

	/**
	 * Refresh Token 생성
	 */
	public String createRefreshToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenValiditySeconds * 1000);

		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key)
			.compact();
	}

	/**
	 * 토큰에서 사용자명 추출
	 */
	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getSubject();
	}

	/**
	 * 토근 유효성 검증
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * 토큰에서 UserDetails 조회
	 */
	public UserDetails getUserDetailsFromToken(String token) {
		String username = getUsernameFromToken(token);
		return userDetailsService.loadUserByUsername(username);
	}

	/**
	 * 토큰 만료 시간 확인
	 */
	public Date getExpirationDateFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getExpiration();
	}

	/**
	 * 토큰이 만료되었는지 확인
	 */
	public boolean isTokenExpired(String token) {
		try {
			Date expiration = getExpirationDateFromToken(token);
			return expiration.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
}
