package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(JwtProperties jwtProperties) throws JOSEException {
        this.issuer = jwtProperties.getIssuer();
        this.accessTokenExpirationMs = jwtProperties.getAccessTokenExpiration() * 1000L;
        this.refreshTokenExpirationMs = jwtProperties.getRefreshTokenExpiration() * 1000L;

        byte[] secretBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);

        this.accessTokenSigner = new MACSigner(secretBytes);
        this.accessTokenVerifier = new MACVerifier(secretBytes);
        this.refreshTokenSigner = new MACSigner(secretBytes);
        this.refreshTokenVerifier = new MACVerifier(secretBytes);
    }

    // Access Token 생성
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    // Refresh Token 생성
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    // 공통 토큰 생성 메서드
    private String generateToken(
            DiscodeitUserDetails userDetails,
            long expirationMs,
            JWSSigner signer,
            String tokenType
    ) throws JOSEException {
        String tokenId = UUID.randomUUID().toString();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .jwtID(tokenId)
                .issuer(issuer)
                .claim("userId", userDetails.getUserResponse().getId())
                .claim("type", tokenType)
                .claim("email", userDetails.getUserResponse().getEmail())
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

        log.debug("Generated {} token for user: {}", tokenType, userDetails.getUsername());
        return token;
    }

    // Access Token 검증
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenVerifier, "access");
    }

    // Refresh Token 검증
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenVerifier, "refresh");
    }

    // 토큰 검증 공통 메서드
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

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userIdStr = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
            return UUID.fromString(userIdStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token or userId", e);
        }
    }

    // Refresh Token을 쿠키로 변환
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(JwtProperties.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);  // JavaScript 접근 불가
        cookie.setSecure(true);    // HTTPS에서만 전송
        cookie.setPath("/");       // 모든 경로에서 접근
        cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000L));
        return cookie;
    }

    // Refresh Token 쿠키 삭제용
    public Cookie generateRefreshTokenExpirationCookie() {
        Cookie cookie = new Cookie(JwtProperties.REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        return cookie;
    }
}