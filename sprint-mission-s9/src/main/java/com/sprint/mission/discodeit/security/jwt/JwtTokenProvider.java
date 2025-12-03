package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {
  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  @Getter
  private final long accessTokenExpirationMs;
  @Getter
  private final long refreshTokenExpirationMs;

  private final String issuer;

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

  // access 토큰 발급
  public String generateAccessToken(String username, UUID userId, List<String> roles) throws JOSEException {
    return generateToken(username, userId, roles, accessTokenExpirationMs, accessTokenSigner, "access");
  }

  // refresh 토큰 발급
  public String generateRefreshToken(String username, UUID userId) throws JOSEException {
    return generateToken(username, userId, null, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
  }

  // 공용 토큰 발급
  private String generateToken(
      String username,
      UUID userId,
      List<String> roles,
      long expirationMs,
      JWSSigner signer,
      String tokenType
  ) throws JOSEException {
    String tokenId = UUID.randomUUID().toString();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
        .subject(username)
        .jwtID(tokenId)
        .issuer(issuer)
        .claim("userId", userId.toString())
        .claim("type", tokenType)
        .issueTime(now)
        .expirationTime(expiryDate);

    if (roles != null && !roles.isEmpty()) {
      claimsBuilder.claim("roles", roles);
    }

    JWTClaimsSet claimsSet = claimsBuilder.build();

    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader(JWSAlgorithm.HS256),
        claimsSet
    );

    signedJWT.sign(signer);
    String token = signedJWT.serialize();

    log.debug("Generated {} token for user: {}", tokenType, username);
    return token;
  }


  // refresh 토큰으로 새 access 토큰 발급
  public String refreshAccessToken(String refreshToken, List<String> roles) throws JOSEException {
    if (!validateRefreshToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid or expired refresh token");
    }

    String username = getUsernameFromToken(refreshToken);
    UUID userId = getUserId(refreshToken);

    return generateAccessToken(username, userId, roles);
  }

  // access 토큰 검증
  public boolean validateAccessToken(String token) {
    return validateToken(token, accessTokenVerifier, "access");
  }

  // refresh 토큰 검증
  public boolean validateRefreshToken(String token) {
    return validateToken(token, refreshTokenVerifier, "refresh");
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
      String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
      if (!expectedType.equals(tokenType)) {
        log.debug("JWT token type mismatch: expected {}, got {}", expectedType, tokenType);
        return false;
      }

      // 3) 만료 시간 검증
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

  // 토큰에서 사용자 이름 추출
  public String getUsernameFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }


  // 토큰에서 토큰 ID 추출
  public String getTokenId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getJWTID();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  // 토큰에서 사용자 ID 추출
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


  // 토큰에서 권한 목록 추출
  @SuppressWarnings("unchecked")
  public List<String> getRolesFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      Object claim = signedJWT.getJWTClaimsSet().getClaim("roles");

      if (claim instanceof List) {
        return (List<String>) claim;
      }

      return List.of();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  // 생성된 Refresh Token을 쿠키로 변환
  public Cookie generateRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000L));
    return cookie;
  }


  // Refresh Token을 무효화하는 쿠키 생성
  public Cookie generateRefreshTokenExpirationCookie() {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    return cookie;
  }
}