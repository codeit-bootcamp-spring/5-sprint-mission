package com.sprint.mission.discodeit.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.sprint.mission.discodeit.domain.dto.user.UserDto;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
	// todo 요구사항과 비교 해야함
	public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

	private final String issuer;
	private final long accessTokenExpirationMs; // 1시간
	private final long refreshTokenExpirationMs; // 하루

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

	/**
	 * Create AccessToken
	 * @param userDetails
	 * @return
	 * @throws JOSEException
	 */
	public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
		TokenComponent tokenComponent = TokenComponent.of(accessTokenExpirationMs, accessTokenSigner, "access");
		return generateToken(userDetails, tokenComponent);
	}

	/**
	 * Create RefreshToken
	 * @param userDetails
	 * @return
	 * @throws JOSEException
	 */
	public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

		TokenComponent tokenComponent = TokenComponent.of(refreshTokenExpirationMs, refreshTokenSigner, "refresh");
		return generateToken(userDetails, tokenComponent);
	}

	/**
	 * generate Cookie with refresh token
	 * @param refreshToken 리프레시 토큰
	 * @return Cookie
	 */
	public Cookie genereateRefreshTokenCookie(String refreshToken) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken); // 키쌍으로 만들어짐
		cookie.setHttpOnly(true); // 브라우저에서 읽기 금지
		cookie.setSecure(true); // https에서만 통용됨
		cookie.setPath("/");
		cookie.setMaxAge((int)(refreshTokenExpirationMs / 1000L)); // 쿠키 저장시간
		return cookie;
	}

	/**
	 * generate Cookie with expired refresh token
	 * @return Cookie
	 */
	public Cookie genereateRefreshTokenExpirationCookie() {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, ""); // 값 지우기
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // 무효화 시간 정하기
		return cookie;
	}

	/**
	 * @param token 토큰
	 * @return Acc 토큰 유효성 검증 결과
	 */
	public boolean validateAccessToken(String token) {
		return validateToken(token, accessTokenVerifier, "access");
	}

	/**
	 * @param token 토큰
	 * @return Refresh 토큰 유효성 검증 결과
	 */
	public boolean validateRefreshToken(String token) {
		return validateToken(token, refreshTokenVerifier, "refresh");
	}

	/**
	 *
	 * @param token accessToken
	 * @return accessToken
	 */
	public String getUsernameFromToken(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			return signedJWT.getJWTClaimsSet().getSubject();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}

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

	private String generateToken(DiscodeitUserDetails userDetails, TokenComponent tokenComponent) throws JOSEException {

		UserDto user = userDetails.getUserDto();
		String tokenType = tokenComponent.tokenType();
		JWSSigner signer = tokenComponent.signer();

		JWTClaimsSet claimsSet = getClaimsFromTokenAndUser(user, tokenComponent, userDetails);

		SignedJWT signedJWT = new SignedJWT(
		  new JWSHeader(JWSAlgorithm.HS256),
		  claimsSet
		);

		signedJWT.sign(signer);
		String token = signedJWT.serialize();

		log.debug("Generated {} token for username: {}", tokenType, user.getUsername());
		return token;
	}

	private JWTClaimsSet getClaimsFromTokenAndUser(
	  UserDto userDto,
	  TokenComponent tokenComponent,
	  DiscodeitUserDetails userDetails
	) throws JOSEException {

		UUID userID = userDto.getId();
		String username = userDto.getUsername();
		UUID tokenId = UUID.randomUUID();
		String tokenType = tokenComponent.tokenType();
		long expirationMs = tokenComponent.expirationMs();

		List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMs);

		return new JWTClaimsSet.Builder()
		  .subject(username)
		  .jwtID(tokenId.toString())
		  .issuer(issuer)
		  .claim("userId", userID) // Long 그대로 저장
		  .claim("type", tokenType)
		  //                .claim("email", user.email())
		  .claim("roles", authorities)
		  .issueTime(now)
		  .expirationTime(expiryDate)
		  .build();

	}
}
