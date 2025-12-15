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
import com.sprint.mission.discodeit.global.config.properties.JwtProperties;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLES = "roles";

    private final JWSSigner accessTokenSigner;
    private final JWSSigner refreshTokenSigner;
    private final List<JWSVerifier> accessTokenVerifiers;
    private final List<JWSVerifier> refreshTokenVerifiers;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        JwtProperties.AccessToken accessConfig = jwtProperties.accessToken();
        JwtProperties.RefreshToken refreshConfig = jwtProperties.refreshToken();

        try {
            this.accessTokenSigner = createSigner(accessConfig.secret());
            this.refreshTokenSigner = createSigner(refreshConfig.secret());
            this.accessTokenVerifiers = createVerifiers(accessConfig);
            this.refreshTokenVerifiers = createVerifiers(refreshConfig);
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to initialize JwtTokenProvider", e);
        }

        this.accessTokenExpiration = accessConfig.expiration();
        this.refreshTokenExpiration = refreshConfig.expiration();

        log.info("JwtTokenProvider initialized: accessVerifiers={}, refreshVerifiers={}",
            accessTokenVerifiers.size(), refreshTokenVerifiers.size());
    }

    private JWSSigner createSigner(String secret) throws JOSEException {
        return new MACSigner(secret.getBytes(StandardCharsets.UTF_8));
    }

    private List<JWSVerifier> createVerifiers(
        JwtProperties.AccessToken config
    ) throws JOSEException {
        return buildVerifierList(
            config.secret(),
            config.hasPreviousSecret() ? config.previousSecret() : null,
            JwtTokenProvider.TOKEN_TYPE_ACCESS
        );
    }

    private List<JWSVerifier> createVerifiers(
        JwtProperties.RefreshToken config
    ) throws JOSEException {
        return buildVerifierList(
            config.secret(),
            config.hasPreviousSecret() ? config.previousSecret() : null,
            JwtTokenProvider.TOKEN_TYPE_REFRESH
        );
    }

    private List<JWSVerifier> buildVerifierList(
        String currentSecret,
        String previousSecret,
        String tokenType
    ) throws JOSEException {
        List<JWSVerifier> verifiers = new ArrayList<>();
        verifiers.add(new MACVerifier(currentSecret.getBytes(StandardCharsets.UTF_8)));

        if (hasText(previousSecret)) {
            verifiers.add(new MACVerifier(previousSecret.getBytes(StandardCharsets.UTF_8)));
            log.info("Previous {} token secret configured (rotation enabled)", tokenType);
        }

        return List.copyOf(verifiers);
    }

    public String generateAccessToken(DiscodeitUserDetails userDetails) {
        return generateToken(userDetails, accessTokenExpiration, accessTokenSigner, TOKEN_TYPE_ACCESS);
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration, refreshTokenSigner, TOKEN_TYPE_REFRESH);
    }

    private String generateToken(
        DiscodeitUserDetails userDetails,
        Duration expiration,
        JWSSigner signer,
        String tokenType
    ) {
        try {
            UserDetailsDto dto = userDetails.getUserDetailsDto();
            Instant now = Instant.now();
            Instant expirationTime = now.plus(expiration);

            List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

            JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(dto.username())
                .jwtID(UUID.randomUUID().toString())
                .claim(CLAIM_USER_ID, dto.id().toString())
                .claim(CLAIM_TYPE, tokenType)
                .claim(CLAIM_ROLES, roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expirationTime))
                .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            signedJWT.sign(signer);

            log.debug("{} token generated: username={}", tokenType, dto.username());
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate " + tokenType + " token", e);
        }
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenVerifiers, TOKEN_TYPE_ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenVerifiers, TOKEN_TYPE_REFRESH);
    }

    private boolean validateToken(String token, List<JWSVerifier> verifiers, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!verifySignature(signedJWT, verifiers, expectedType)
                || !verifyTokenType(signedJWT, expectedType)) {
                return false;
            }
            return verifyExpiration(signedJWT, expectedType);
        } catch (ParseException e) {
            log.debug("JWT {} token parsing failed: {}", expectedType, e.getMessage());
        } catch (Exception e) {
            log.debug("JWT {} token validation failed: {}", expectedType, e.getMessage());
        }
        return false;
    }

    private boolean verifySignature(SignedJWT jwt, List<JWSVerifier> verifiers, String tokenType)
        throws JOSEException {
        for (int i = 0; i < verifiers.size(); i++) {
            if (jwt.verify(verifiers.get(i))) {
                if (i > 0) {
                    log.info("JWT {} token verified with previous secret (rotation in progress)", tokenType);
                }
                return true;
            }
        }
        log.debug("JWT {} token signature verification failed: verifiers={}", tokenType, verifiers.size());
        return false;
    }

    private boolean verifyTokenType(SignedJWT jwt, String expectedType) throws ParseException {
        Object typeClaim = jwt.getJWTClaimsSet().getClaim(CLAIM_TYPE);
        if (typeClaim == null || !expectedType.equals(typeClaim.toString())) {
            log.debug("JWT token type mismatch: expected={}, actual={}", expectedType, typeClaim);
            return false;
        }
        return true;
    }

    private boolean verifyExpiration(SignedJWT jwt, String tokenType) throws ParseException {
        Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || expirationTime.toInstant().isBefore(Instant.now())) {
            log.debug("JWT {} token expired", tokenType);
            return false;
        }
        return true;
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getTokenIdFromToken(String token) {
        return parseToken(token).getJWTID();
    }

    public UUID getUserIdFromToken(String token) {
        JWTClaimsSet claims = parseToken(token);
        String userIdStr = (String) claims.getClaim(CLAIM_USER_ID);
        if (userIdStr == null) {
            throw new IllegalArgumentException("User ID claim not found in JWT token");
        }
        return UUID.fromString(userIdStr);
    }

    private JWTClaimsSet parseToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
