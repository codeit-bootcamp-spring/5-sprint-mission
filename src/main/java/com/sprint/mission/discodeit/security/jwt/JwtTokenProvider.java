package com.sprint.mission.discodeit.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

// 토큰을 발급하고, 검증하는 기능성 util 클래스
@Slf4j
@Component
public class JwtTokenProvider {
	// 브라우저에 내려줄 리프레시 토큰 쿠키 이름
	public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

	// ms 단위로 저장
	private final long accessTokenExpirationMs;
	private final long refreshTokenExpirationMs;

	// 토큰 발급자(issuer) 식별용 문자열
	private final String issuer;

	// Access / Refresh 각각에 사용할 서명/검증 객체
	private final JWSSigner accessTokenSigner;
	private final JWSVerifier accessTokenVerifier;
	private final JWSSigner refreshTokenSigner;
	private final JWSVerifier refreshTokenVerifier;

	public JwtTokenProvider(
		@Value("${security.jwt.secret}") String secret,
		@Value("${security.jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
		@Value("${security.jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
		@Value("${security.jwt.issuer}") String issuer
	) throws JOSEException {
		this.issuer = issuer;
		this.accessTokenExpirationMs = accessTokenValiditySeconds * 1000L;
		this.refreshTokenExpirationMs = refreshTokenValiditySeconds * 1000L;

		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

		this.accessTokenSigner = new MACSigner(secretBytes);
		this.accessTokenVerifier = new MACVerifier(secretBytes);
		this.refreshTokenSigner = new MACSigner(secretBytes);
		this.refreshTokenVerifier = new MACVerifier(secretBytes);
	}

	// Access Token 생성 메서드(단기, json으로 응답 예정)
	public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
		return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
	}

	// Refresh Token 생성 메서드(장기, 쿠키로 응답 예정)
	public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
		return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
	}

	// 공용 토큰 만드는 메서드
	private String generateToken(
		DiscodeitUserDetails userDetails,
		long expirationMs,
		JWSSigner signer,
		String tokenType
	) throws JOSEException {
		String tokenId = UUID.randomUUID().toString();
		UserDto user = userDetails.getUserDto();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMs);

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject(user.username())
			.jwtID(tokenId)
			.issuer(issuer)
			.claim("userId", user.id().toString())
			.claim("type", tokenType)
			.claim("roles", userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()))
			.issueTime(now)
			.expirationTime(expiryDate)
			.build();

		SignedJWT signedJWT = new SignedJWT(
			new JWSHeader(JWSAlgorithm.HS256),
			claimsSet
		);

		signedJWT.sign(signer);
		String token = signedJWT.serialize();

		log.debug("Generated {} token for user: {}", tokenType, user.username());
		return token;
	}

	// Access  토큰 검증
	public boolean validateAccessToken(String token) {
		return validateToken(token, accessTokenVerifier, "access");
	}

	// Refresh  토큰 검증
	public boolean validateRefreshToken(String token) {
		return validateToken(token, refreshTokenVerifier, "refresh");
	}

	// 토큰 검증 공용
	private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);

			// 1) 서명 검증
			if (!signedJWT.verify(verifier)) {
				log.debug("JWT signature verification failed for {} token", expectedType);
				return false;
			}

			// 2) type 클레임 검증
			String tokenType = (String)signedJWT.getJWTClaimsSet().getClaim("type");
			if (!expectedType.equals(tokenType)) {
				log.debug("JWT token type mismatch: expected {}, got {}", expectedType, tokenType);
				return false;
			}

			// 3) 만료 시간
			Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
			if (expirationTime == null || expirationTime.before(new Date())) {
				log.debug("JWT {} token expired", expectedType);
				return false;
			}

			return true;
		} catch (Exception e) {
			log.debug("JWT {} token validation failed: {}", expectedType, e.getMessage());
			return false;
		}
	}

	// username 파싱
	public String getUsernameFromToken(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			return signedJWT.getJWTClaimsSet().getSubject();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}

	public String getTokenId(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			return signedJWT.getJWTClaimsSet().getJWTID();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}

	public UUID getUserId(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			Object claim = signedJWT.getJWTClaimsSet().getClaim("userId");

			if (claim instanceof String str) {
				return UUID.fromString(str);
			}

			throw new IllegalArgumentException("User ID claim not found in JWT token");
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}

	// 생성된 Refresh  토큰을 쿠키로 변환하는 과정
	public Cookie genereateRefreshTokenCookie(String refreshToken) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken); // 키쌍으로 만들어짐
		cookie.setHttpOnly(true); // 브라우저에서 읽기 금지
		cookie.setSecure(true); // https에서만 통용됨
		cookie.setPath("/");
		cookie.setMaxAge((int)(refreshTokenExpirationMs / 1000L)); // 쿠키 저장시간
		return cookie;
	}

	// 생성된 Refresh 무효화 하는 쿠키로 만드는 과정
	public Cookie genereateRefreshTokenExpirationCookie() {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, ""); // 값 지우기
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // 무효화 시간 정하기
		return cookie;
	}
}
