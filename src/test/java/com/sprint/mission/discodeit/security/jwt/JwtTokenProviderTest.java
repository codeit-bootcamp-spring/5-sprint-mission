package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.config.properties.JwtProperties;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createDiscodeitUserDetails;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private static final String ACCESS_SECRET = "test-access-secret-key-must-be-at-least-32-bytes";
    private static final String REFRESH_SECRET = "test-refresh-secret-key-must-be-at-least-32-bytes";
    private static final int ACCESS_EXPIRATION_MS = 1800000;
    private static final int REFRESH_EXPIRATION_MS = 604800000;

    private JwtTokenProvider tokenProvider;
    private DiscodeitUserDetails userDetails;

    @BeforeEach
    void setUp() throws JOSEException {
        JwtProperties.AccessToken accessToken = new JwtProperties.AccessToken(
            ACCESS_SECRET, null, ACCESS_EXPIRATION_MS);
        JwtProperties.RefreshToken refreshToken = new JwtProperties.RefreshToken(
            REFRESH_SECRET, null, REFRESH_EXPIRATION_MS);
        JwtProperties jwtProperties = new JwtProperties(accessToken, refreshToken, 1);

        tokenProvider = new JwtTokenProvider(jwtProperties);

        UserDto userDto = createUserDto(UUID.randomUUID(), "testuser", "test@example.com");
        userDetails = createDiscodeitUserDetails(userDto);
    }

    @Test
    @DisplayName("generateAccessToken - Access 토큰을 생성한다")
    void generateAccessToken_GeneratesToken() throws JOSEException {
        // when
        String token = tokenProvider.generateAccessToken(userDetails);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("generateRefreshToken - Refresh 토큰을 생성한다")
    void generateRefreshToken_GeneratesToken() throws JOSEException {
        // when
        String token = tokenProvider.generateRefreshToken(userDetails);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("validateAccessToken - 유효한 Access 토큰은 true를 반환한다")
    void validateAccessToken_ValidToken_ReturnsTrue() throws JOSEException {
        // given
        String token = tokenProvider.generateAccessToken(userDetails);

        // when
        boolean valid = tokenProvider.validateAccessToken(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("validateAccessToken - Refresh 토큰은 false를 반환한다")
    void validateAccessToken_RefreshToken_ReturnsFalse() throws JOSEException {
        // given
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // when
        boolean valid = tokenProvider.validateAccessToken(refreshToken);

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("validateRefreshToken - 유효한 Refresh 토큰은 true를 반환한다")
    void validateRefreshToken_ValidToken_ReturnsTrue() throws JOSEException {
        // given
        String token = tokenProvider.generateRefreshToken(userDetails);

        // when
        boolean valid = tokenProvider.validateRefreshToken(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("validateRefreshToken - Access 토큰은 false를 반환한다")
    void validateRefreshToken_AccessToken_ReturnsFalse() throws JOSEException {
        // given
        String accessToken = tokenProvider.generateAccessToken(userDetails);

        // when
        boolean valid = tokenProvider.validateRefreshToken(accessToken);

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("validateAccessToken - 잘못된 토큰은 false를 반환한다")
    void validateAccessToken_InvalidToken_ReturnsFalse() {
        // when
        boolean valid = tokenProvider.validateAccessToken("invalid.token.here");

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("getUsernameFromToken - 토큰에서 username을 추출한다")
    void getUsernameFromToken_ExtractsUsername() throws JOSEException {
        // given
        String token = tokenProvider.generateAccessToken(userDetails);

        // when
        String username = tokenProvider.getUsernameFromToken(token);

        // then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUserId - 토큰에서 userId를 추출한다")
    void getUserId_ExtractsUserId() throws JOSEException {
        // given
        String token = tokenProvider.generateAccessToken(userDetails);
        UUID expectedUserId = userDetails.getUserDetailsDto().id();

        // when
        UUID userId = tokenProvider.getUserId(token);

        // then
        assertThat(userId).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("getTokenId - 토큰에서 jti를 추출한다")
    void getTokenId_ExtractsJti() throws JOSEException {
        // given
        String token = tokenProvider.generateAccessToken(userDetails);

        // when
        String tokenId = tokenProvider.getTokenId(token);

        // then
        assertThat(tokenId).isNotNull();
        assertThat(UUID.fromString(tokenId)).isNotNull();
    }

    @Test
    @DisplayName("getUsernameFromToken - 잘못된 토큰은 예외를 던진다")
    void getUsernameFromToken_InvalidToken_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> tokenProvider.getUsernameFromToken("invalid"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("generateRefreshTokenCookie - HttpOnly 쿠키를 생성한다")
    void generateRefreshTokenCookie_CreatesHttpOnlyCookie() throws JOSEException {
        // given
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // when
        Cookie cookie = tokenProvider.generateRefreshTokenCookie(refreshToken);

        // then
        assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
        assertThat(cookie.getValue()).isEqualTo(refreshToken);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(REFRESH_EXPIRATION_MS / 1000);
    }

    @Test
    @DisplayName("generateRefreshTokenExpirationCookie - 만료 쿠키를 생성한다")
    void generateRefreshTokenExpirationCookie_CreatesExpiredCookie() {
        // when
        Cookie cookie = tokenProvider.generateRefreshTokenExpirationCookie();

        // then
        assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    @DisplayName("이전 시크릿으로 서명된 토큰도 검증할 수 있다")
    void validateToken_WithPreviousSecret_ReturnsTrue() throws JOSEException {
        // given - 이전 시크릿으로 토큰 생성
        String previousSecret = "previous-access-secret-key-must-be-32-bytes!!";
        JwtProperties.AccessToken accessToken = new JwtProperties.AccessToken(
            previousSecret, null, ACCESS_EXPIRATION_MS);
        JwtProperties.RefreshToken refreshToken = new JwtProperties.RefreshToken(
            REFRESH_SECRET, null, REFRESH_EXPIRATION_MS);
        JwtProperties oldProperties = new JwtProperties(accessToken, refreshToken, 1);
        JwtTokenProvider oldProvider = new JwtTokenProvider(oldProperties);

        String token = oldProvider.generateAccessToken(userDetails);

        // 새 프로바이더는 이전 시크릿을 previousSecret으로 설정
        JwtProperties.AccessToken newAccessToken = new JwtProperties.AccessToken(
            ACCESS_SECRET, previousSecret, ACCESS_EXPIRATION_MS);
        JwtProperties.RefreshToken newRefreshToken = new JwtProperties.RefreshToken(
            REFRESH_SECRET, null, REFRESH_EXPIRATION_MS);
        JwtProperties newProperties = new JwtProperties(newAccessToken, newRefreshToken, 1);
        JwtTokenProvider newProvider = new JwtTokenProvider(newProperties);

        // when
        boolean valid = newProvider.validateAccessToken(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Refresh 토큰도 이전 시크릿으로 검증할 수 있다")
    void validateRefreshToken_WithPreviousSecret_ReturnsTrue() throws JOSEException {
        // given - 이전 시크릿으로 refresh 토큰 생성
        String previousRefreshSecret = "previous-refresh-secret-key-must-be-32-bytes!";
        JwtProperties.AccessToken accessToken = new JwtProperties.AccessToken(
            ACCESS_SECRET, null, ACCESS_EXPIRATION_MS);
        JwtProperties.RefreshToken refreshToken = new JwtProperties.RefreshToken(
            previousRefreshSecret, null, REFRESH_EXPIRATION_MS);
        JwtProperties oldProperties = new JwtProperties(accessToken, refreshToken, 1);
        JwtTokenProvider oldProvider = new JwtTokenProvider(oldProperties);

        String token = oldProvider.generateRefreshToken(userDetails);

        // 새 프로바이더는 이전 시크릿을 previousSecret으로 설정
        JwtProperties.AccessToken newAccessToken = new JwtProperties.AccessToken(
            ACCESS_SECRET, null, ACCESS_EXPIRATION_MS);
        JwtProperties.RefreshToken newRefreshToken = new JwtProperties.RefreshToken(
            REFRESH_SECRET, previousRefreshSecret, REFRESH_EXPIRATION_MS);
        JwtProperties newProperties = new JwtProperties(newAccessToken, newRefreshToken, 1);
        JwtTokenProvider newProvider = new JwtTokenProvider(newProperties);

        // when
        boolean valid = newProvider.validateRefreshToken(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("validateAccessToken - 만료된 토큰은 false를 반환한다")
    void validateAccessToken_ExpiredToken_ReturnsFalse() throws JOSEException {
        // given - 이미 만료된 토큰 직접 생성
        Date now = new Date();
        Date pastDate = new Date(now.getTime() - 10000);

        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
            .subject("testuser")
            .jwtID(UUID.randomUUID().toString())
            .claim("userId", UUID.randomUUID().toString())
            .claim("type", "access")
            .issueTime(pastDate)
            .expirationTime(pastDate)
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claimSet
        );
        signedJWT.sign(new MACSigner(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8)));
        String expiredToken = signedJWT.serialize();

        // when
        boolean valid = tokenProvider.validateAccessToken(expiredToken);

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("getTokenId - 잘못된 토큰은 예외를 던진다")
    void getTokenId_InvalidToken_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> tokenProvider.getTokenId("invalid.token"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid JWT token");
    }

    @Test
    @DisplayName("getUserId - 잘못된 토큰은 예외를 던진다")
    void getUserId_InvalidToken_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> tokenProvider.getUserId("invalid.token"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid JWT token");
    }

    @Test
    @DisplayName("getUserId - userId claim이 없는 토큰은 예외를 던진다")
    void getUserId_MissingUserIdClaim_ThrowsException() throws JOSEException {
        // given - userId claim이 없는 토큰 생성
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_EXPIRATION_MS);

        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
            .subject("testuser")
            .jwtID(UUID.randomUUID().toString())
            .claim("type", "access")
            .issueTime(now)
            .expirationTime(expiryDate)
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claimSet
        );
        signedJWT.sign(new MACSigner(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8)));
        String tokenWithoutUserId = signedJWT.serialize();

        // when & then - 201-202에서 throw된 예외가 206에서 catch되어 다시 throw됨
        assertThatThrownBy(() -> tokenProvider.getUserId(tokenWithoutUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid JWT token")
            .hasRootCauseMessage("User ID claim not found in JWT token");
    }
}
