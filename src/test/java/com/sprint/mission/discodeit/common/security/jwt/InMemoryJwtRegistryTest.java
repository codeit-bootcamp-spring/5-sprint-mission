package com.sprint.mission.discodeit.common.security.jwt;

import com.sprint.mission.discodeit.common.config.properties.JwtProperties;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("InMemoryJwtRegistry 단위 테스트")
class InMemoryJwtRegistryTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    private InMemoryJwtRegistry jwtRegistry;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        JwtProperties.AccessToken accessToken = new JwtProperties.AccessToken(
            "test-secret-32-bytes-long-key!!", null, 1800000);
        JwtProperties.RefreshToken refreshToken = new JwtProperties.RefreshToken(
            "test-secret-32-bytes-long-key!!", null, 604800000);
        JwtProperties jwtProperties = new JwtProperties(accessToken, refreshToken, 2);

        jwtRegistry = new InMemoryJwtRegistry(tokenProvider, jwtProperties);

        userId = UUID.randomUUID();
        userDto = createUserDto(userId, "testuser", "test@example.com");
    }

    @Test
    @DisplayName("registerJwtInformation - JWT 정보를 등록한다")
    void registerJwtInformation_RegistersInfo() {
        // given
        JwtInformation jwtInfo = new JwtInformation(userDto, "access-token", "refresh-token");

        // when
        jwtRegistry.registerJwtInformation(jwtInfo);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-token")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-token")).isTrue();
    }

    @Test
    @DisplayName("registerJwtInformation - maxSessions 초과 시 가장 오래된 JWT를 제거한다")
    void registerJwtInformation_RemovesOldestWhenExceedsMaxSessions() {
        // given
        JwtInformation jwt1 = new JwtInformation(userDto, "access-1", "refresh-1");
        JwtInformation jwt2 = new JwtInformation(userDto, "access-2", "refresh-2");
        JwtInformation jwt3 = new JwtInformation(userDto, "access-3", "refresh-3");

        // when
        jwtRegistry.registerJwtInformation(jwt1);
        jwtRegistry.registerJwtInformation(jwt2);
        jwtRegistry.registerJwtInformation(jwt3);

        // then - jwt1은 제거되고, jwt2와 jwt3만 남음
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-3")).isTrue();
    }

    @Test
    @DisplayName("invalidateJwtInformationByUserId - 사용자의 모든 JWT를 무효화한다")
    void invalidateJwtInformationByUserId_InvalidatesAllUserJwts() {
        // given
        JwtInformation jwt1 = new JwtInformation(userDto, "access-1", "refresh-1");
        JwtInformation jwt2 = new JwtInformation(userDto, "access-2", "refresh-2");
        jwtRegistry.registerJwtInformation(jwt1);
        jwtRegistry.registerJwtInformation(jwt2);

        // when
        jwtRegistry.invalidateJwtInformationByUserId(userId);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isFalse();
    }

    @Test
    @DisplayName("hasActiveJwtInformationByUserId - 등록되지 않은 사용자는 false")
    void hasActiveJwtInformationByUserId_UnknownUser_ReturnsFalse() {
        // when
        boolean hasActive = jwtRegistry.hasActiveJwtInformationByUserId(UUID.randomUUID());

        // then
        assertThat(hasActive).isFalse();
    }

    @Test
    @DisplayName("hasActiveJwtInformationByAccessToken - 등록되지 않은 토큰은 false")
    void hasActiveJwtInformationByAccessToken_UnknownToken_ReturnsFalse() {
        // when
        boolean hasActive = jwtRegistry.hasActiveJwtInformationByAccessToken("unknown-token");

        // then
        assertThat(hasActive).isFalse();
    }

    @Test
    @DisplayName("rotateJwtInformation - JWT 정보를 교체한다")
    void rotateJwtInformation_RotatesJwtInfo() {
        // given
        JwtInformation oldJwt = new JwtInformation(userDto, "old-access", "old-refresh");
        jwtRegistry.registerJwtInformation(oldJwt);

        JwtInformation newJwt = new JwtInformation(userDto, "new-access", "new-refresh");

        // when
        jwtRegistry.rotateJwtInformation("old-refresh", newJwt);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("old-access")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("old-refresh")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("new-access")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("new-refresh")).isTrue();
    }

    @Test
    @DisplayName("rotateJwtInformation - 존재하지 않는 refresh 토큰이면 아무것도 하지 않는다")
    void rotateJwtInformation_UnknownRefreshToken_DoesNothing() {
        // given
        JwtInformation newJwt = new JwtInformation(userDto, "new-access", "new-refresh");

        // when
        jwtRegistry.rotateJwtInformation("unknown-refresh", newJwt);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("new-access")).isFalse();
    }

    @Test
    @DisplayName("clearExpiredJwtInformation - 만료된 JWT를 정리한다")
    void clearExpiredJwtInformation_ClearsExpiredJwts() {
        // given
        JwtInformation validJwt = new JwtInformation(userDto, "valid-access", "valid-refresh");
        JwtInformation expiredJwt = new JwtInformation(userDto, "expired-access", "expired-refresh");
        jwtRegistry.registerJwtInformation(validJwt);
        jwtRegistry.registerJwtInformation(expiredJwt);

        given(tokenProvider.validateRefreshToken("valid-refresh")).willReturn(true);
        given(tokenProvider.validateRefreshToken("expired-refresh")).willReturn(false);

        // when
        jwtRegistry.clearExpiredJwtInformation();

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("valid-access")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("expired-access")).isFalse();
    }

    @Test
    @DisplayName("여러 사용자의 JWT를 독립적으로 관리한다")
    void multipleUsers_ManagedIndependently() {
        // given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UserDto user1Dto = createUserDto(user1Id, "user1", "user1@example.com");
        UserDto user2Dto = createUserDto(user2Id, "user2", "user2@example.com");

        JwtInformation user1Jwt = new JwtInformation(user1Dto, "user1-access", "user1-refresh");
        JwtInformation user2Jwt = new JwtInformation(user2Dto, "user2-access", "user2-refresh");

        jwtRegistry.registerJwtInformation(user1Jwt);
        jwtRegistry.registerJwtInformation(user2Jwt);

        // when
        jwtRegistry.invalidateJwtInformationByUserId(user1Id);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(user1Id)).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(user2Id)).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("user2-access")).isTrue();
    }

    @Test
    @DisplayName("rotateJwtInformation - 여러 사용자 중 올바른 사용자의 JWT만 교체한다")
    void rotateJwtInformation_WithMultipleUsers_RotatesCorrectUser() {
        // given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UserDto user1Dto = createUserDto(user1Id, "user1", "user1@example.com");
        UserDto user2Dto = createUserDto(user2Id, "user2", "user2@example.com");

        JwtInformation user1Jwt = new JwtInformation(user1Dto, "user1-access", "user1-refresh");
        JwtInformation user2Jwt = new JwtInformation(user2Dto, "user2-access", "user2-refresh");

        jwtRegistry.registerJwtInformation(user1Jwt);
        jwtRegistry.registerJwtInformation(user2Jwt);

        JwtInformation newUser2Jwt = new JwtInformation(user2Dto, "user2-new-access", "user2-new-refresh");

        // when - user2의 refresh token으로 rotate (user1의 queue를 먼저 검사하고 return false)
        jwtRegistry.rotateJwtInformation("user2-refresh", newUser2Jwt);

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("user1-access")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("user1-refresh")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("user2-access")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("user2-refresh")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("user2-new-access")).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("user2-new-refresh")).isTrue();
    }

    @Test
    @DisplayName("clearExpiredJwtInformation - 모든 JWT가 만료되면 사용자를 origin에서 제거한다")
    void clearExpiredJwtInformation_RemovesUserWhenAllJwtsExpired() {
        // given
        JwtInformation expiredJwt1 = new JwtInformation(userDto, "expired-access-1", "expired-refresh-1");
        JwtInformation expiredJwt2 = new JwtInformation(userDto, "expired-access-2", "expired-refresh-2");
        jwtRegistry.registerJwtInformation(expiredJwt1);
        jwtRegistry.registerJwtInformation(expiredJwt2);

        given(tokenProvider.validateRefreshToken("expired-refresh-1")).willReturn(false);
        given(tokenProvider.validateRefreshToken("expired-refresh-2")).willReturn(false);

        // when
        jwtRegistry.clearExpiredJwtInformation();

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("expired-access-1")).isFalse();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("expired-access-2")).isFalse();
    }

    @Test
    @DisplayName("invalidateJwtInformationByUserId - 존재하지 않는 사용자를 무효화해도 에러가 발생하지 않는다")
    void invalidateJwtInformationByUserId_UnknownUser_DoesNothing() {
        // given
        UUID unknownUserId = UUID.randomUUID();

        // when & then (no exception)
        jwtRegistry.invalidateJwtInformationByUserId(unknownUserId);

        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(unknownUserId)).isFalse();
    }

    @Test
    @DisplayName("clearExpiredJwtInformation - 만료된 JWT가 없으면 아무것도 제거하지 않는다")
    void clearExpiredJwtInformation_NoExpiredJwts_DoesNothing() {
        // given
        JwtInformation validJwt = new JwtInformation(userDto, "valid-access", "valid-refresh");
        jwtRegistry.registerJwtInformation(validJwt);

        given(tokenProvider.validateRefreshToken("valid-refresh")).willReturn(true);

        // when
        jwtRegistry.clearExpiredJwtInformation();

        // then
        assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isTrue();
        assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("valid-access")).isTrue();
    }
}
