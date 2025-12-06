package com.sprint.mission.discodeit.global.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.UserDetailsDto;
import com.sprint.mission.discodeit.global.config.properties.JwtProperties;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Component
@Slf4j
public class JwtTokenProvider {


    private final JWSSigner accessTokenSigner;
    private final List<JWSVerifier> accessTokenVerifiers;
    private final JWSSigner refreshTokenSigner;
    private final List<JWSVerifier> refreshTokenVerifiers;

    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        JwtProperties.AccessToken accessTokenConfig = jwtProperties.accessToken();
        JwtProperties.RefreshToken refreshTokenConfig = jwtProperties.refreshToken();

        byte[] accessSecretBytes = accessTokenConfig.secret().getBytes(StandardCharsets.UTF_8);
        byte[] refreshSecretBytes = refreshTokenConfig.secret().getBytes(StandardCharsets.UTF_8);

        try {
            this.accessTokenSigner = new MACSigner(accessSecretBytes);
            this.refreshTokenSigner = new MACSigner(refreshSecretBytes);

            this.accessTokenVerifiers = buildVerifiers(
                accessSecretBytes,
                accessTokenConfig.hasPreviousSecret() ? accessTokenConfig.previousSecret() : null,
                "access"
            );
            this.refreshTokenVerifiers = buildVerifiers(
                refreshSecretBytes,
                refreshTokenConfig.hasPreviousSecret() ? refreshTokenConfig.previousSecret() : null,
                "refresh"
            );
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to initialize JwtTokenProvider", e);
        }

        this.accessTokenExpiration = accessTokenConfig.expiration();
        this.refreshTokenExpiration = refreshTokenConfig.expiration();

        log.info("JwtTokenProvider 초기화: accessVerifiers={}, refreshVerifiers={}",
            accessTokenVerifiers.size(), refreshTokenVerifiers.size());
    }

    private List<JWSVerifier> buildVerifiers(
        byte[] currentSecret,
        String previousSecret,
        String tokenType
    ) throws JOSEException {
        List<JWSVerifier> verifiers = new ArrayList<>();
        verifiers.add(new MACVerifier(currentSecret));

        if (hasText(previousSecret)) {
            byte[] previousSecretBytes = previousSecret.getBytes(StandardCharsets.UTF_8);
            verifiers.add(new MACVerifier(previousSecretBytes));
            log.info("이전 {} 토큰 시크릿 설정됨 (rotation 지원)", tokenType);
        }

        return verifiers;
    }

    public String generateAccessToken(DiscodeitUserDetails userDetails) {
        try {
            return generateToken(userDetails, accessTokenExpiration, accessTokenSigner, "access");
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate access token", e);
        }
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails) {
        try {
            return generateToken(userDetails, refreshTokenExpiration, refreshTokenSigner, "refresh");
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate refresh token", e);
        }
    }

    private String generateToken(
        DiscodeitUserDetails userDetails,
        Duration expiration,
        JWSSigner signer,
        String tokenType
    ) throws JOSEException {
        String tokenId = UUID.randomUUID().toString();
        UserDetailsDto userDetailsDto = userDetails.getUserDetailsDto();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration.toMillis());

        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
            .subject(userDetailsDto.username())
            .jwtID(tokenId)
            .claim("userId", userDetailsDto.id().toString())
            .claim("type", tokenType)
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .issueTime(now)
            .expirationTime(expirationDate)
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claimSet
        );

        signedJWT.sign(signer);
        String token = signedJWT.serialize();

        log.debug("{} 토큰 생성 완료: username={}", tokenType, userDetailsDto.username());
        return token;
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenVerifiers, "access");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenVerifiers, "refresh");
    }

    private boolean validateToken(String token, List<JWSVerifier> verifiers, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            boolean signatureValid = false;
            int verifierIndex = 0;
            for (JWSVerifier verifier : verifiers) {
                if (signedJWT.verify(verifier)) {
                    signatureValid = true;
                    if (verifierIndex > 0) {
                        log.info("JWT {} 토큰이 이전 시크릿으로 검증됨 (rotation 진행 중)", expectedType);
                    }
                    break;
                }
                verifierIndex++;
            }

            if (!signatureValid) {
                log.debug("JWT {} 토큰 서명 검증 실패: verifiers={}", expectedType, verifiers.size());
                return false;
            }

            String tokenType = signedJWT.getJWTClaimsSet().getClaim("type").toString();
            if (!expectedType.equals(tokenType)) {
                log.debug("JWT 토큰 타입 불일치: expected={}, actual={}", expectedType, tokenType);
                return false;
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                log.debug("JWT {} 토큰 만료됨", expectedType);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.debug("JWT {} 토큰 검증 실패: {}", expectedType, e.getMessage());
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
            String userIdStr = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
            if (userIdStr == null) {
                throw new IllegalArgumentException("User ID claim not found in JWT token");
            }
            return UUID.fromString(userIdStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
