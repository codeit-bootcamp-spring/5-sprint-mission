package com.sprint.mission.discodeit.global.security.jwt.registry.impl;

import com.sprint.mission.discodeit.global.config.properties.JwtProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("InMemoryJwtRegistry 단위 테스트")
class InMemoryJwtRegistryTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    private InMemoryJwtRegistry jwtRegistry;

    private static final int MAX_SESSIONS = 2;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        given(jwtProperties.maxSessions()).willReturn(MAX_SESSIONS);
        jwtRegistry = new InMemoryJwtRegistry(tokenProvider, jwtProperties);
    }

    private JwtDto createJwtDto(UUID userId, String username, String accessToken, String refreshToken) {
        UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);
        return new JwtDto(userDetailsDto, accessToken, refreshToken);
    }

    @Nested
    @DisplayName("registerJwtInformation")
    class RegisterJwtInformation {

        @Test
        @DisplayName("JWT 정보 등록 시 성공")
        void registerJwtInformation_success() {
            // given
            JwtDto jwtDto = createJwtDto(USER_ID, USERNAME, "access-token-1", "refresh-token-1");

            // when
            jwtRegistry.registerJwtInformation(jwtDto);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-token-1")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-token-1")).isTrue();
        }

        @Test
        @DisplayName("동일 사용자로 여러 JWT 등록 시 maxSessions 만큼 유지")
        void registerJwtInformation_multipleSessions_maintainsMaxSessions() {
            // given
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(USER_ID, USERNAME, "access-2", "refresh-2");

            // when
            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
        }

        @Test
        @DisplayName("maxSessions 초과 시 가장 오래된 JWT 제거")
        void registerJwtInformation_exceedsMaxSessions_removesOldest() {
            // given
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(USER_ID, USERNAME, "access-2", "refresh-2");
            JwtDto jwtDto3 = createJwtDto(USER_ID, USERNAME, "access-3", "refresh-3");

            // when
            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);
            jwtRegistry.registerJwtInformation(jwtDto3);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-1")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-3")).isTrue();
        }

        @Test
        @DisplayName("다른 사용자의 JWT는 독립적으로 관리")
        void registerJwtInformation_differentUsers_managedIndependently() {
            // given
            UUID anotherUserId = UUID.randomUUID();
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(anotherUserId, "anotheruser", "access-2", "refresh-2");

            // when
            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(anotherUserId)).isTrue();
            assertThat(jwtRegistry.getActiveUserIds()).containsExactlyInAnyOrder(USER_ID, anotherUserId);
        }
    }

    @Nested
    @DisplayName("invalidateJwtInformationByUserId")
    class InvalidateJwtInformationByUserId {

        @Test
        @DisplayName("사용자 ID로 모든 JWT 무효화")
        void invalidateJwtInformationByUserId_removesAllUserJwts() {
            // given
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(USER_ID, USERNAME, "access-2", "refresh-2");
            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // when
            jwtRegistry.invalidateJwtInformationByUserId(USER_ID);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-1")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-2")).isFalse();
        }

        @Test
        @DisplayName("다른 사용자의 JWT는 영향받지 않음")
        void invalidateJwtInformationByUserId_doesNotAffectOtherUsers() {
            // given
            UUID anotherUserId = UUID.randomUUID();
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(anotherUserId, "anotheruser", "access-2", "refresh-2");
            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // when
            jwtRegistry.invalidateJwtInformationByUserId(USER_ID);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(anotherUserId)).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 무효화 시 아무 동작 없음")
        void invalidateJwtInformationByUserId_nonExistingUser_noOp() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();

            // when & then (예외 없음)
            jwtRegistry.invalidateJwtInformationByUserId(nonExistingUserId);
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(nonExistingUserId)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasActiveJwtInformation")
    class HasActiveJwtInformation {

        @Test
        @DisplayName("등록된 사용자 ID로 조회 시 true 반환")
        void hasActiveJwtInformationByUserId_existingUser_returnsTrue() {
            // given
            JwtDto jwtDto = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            jwtRegistry.registerJwtInformation(jwtDto);

            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 사용자 ID로 조회 시 false 반환")
        void hasActiveJwtInformationByUserId_nonExistingUser_returnsFalse() {
            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(UUID.randomUUID())).isFalse();
        }

        @Test
        @DisplayName("등록된 액세스 토큰으로 조회 시 true 반환")
        void hasActiveJwtInformationByAccessToken_existingToken_returnsTrue() {
            // given
            JwtDto jwtDto = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            jwtRegistry.registerJwtInformation(jwtDto);

            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 액세스 토큰으로 조회 시 false 반환")
        void hasActiveJwtInformationByAccessToken_nonExistingToken_returnsFalse() {
            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("non-existing-token")).isFalse();
        }

        @Test
        @DisplayName("등록된 리프레시 토큰으로 조회 시 true 반환")
        void hasActiveJwtInformationByRefreshToken_existingToken_returnsTrue() {
            // given
            JwtDto jwtDto = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            jwtRegistry.registerJwtInformation(jwtDto);

            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-1")).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 리프레시 토큰으로 조회 시 false 반환")
        void hasActiveJwtInformationByRefreshToken_nonExistingToken_returnsFalse() {
            // when & then
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("non-existing-token")).isFalse();
        }
    }

    @Nested
    @DisplayName("rotateJwtInformation")
    class RotateJwtInformation {

        @Test
        @DisplayName("리프레시 토큰 회전 시 기존 토큰 제거 및 새 토큰 등록")
        void rotateJwtInformation_success() {
            // given
            JwtDto oldJwtDto = createJwtDto(USER_ID, USERNAME, "old-access", "old-refresh");
            JwtDto newJwtDto = createJwtDto(USER_ID, USERNAME, "new-access", "new-refresh");
            jwtRegistry.registerJwtInformation(oldJwtDto);

            // when
            jwtRegistry.rotateJwtInformation("old-refresh", newJwtDto);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("old-access")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("old-refresh")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("new-access")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("new-refresh")).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 리프레시 토큰으로 회전 시 새 토큰 등록되지 않음")
        void rotateJwtInformation_nonExistingToken_noRegistration() {
            // given
            JwtDto newJwtDto = createJwtDto(USER_ID, USERNAME, "new-access", "new-refresh");

            // when
            jwtRegistry.rotateJwtInformation("non-existing-refresh", newJwtDto);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("new-access")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("new-refresh")).isFalse();
        }

        @Test
        @DisplayName("회전 시 다른 사용자의 토큰에 영향 없음")
        void rotateJwtInformation_doesNotAffectOtherUsers() {
            // given
            UUID anotherUserId = UUID.randomUUID();
            JwtDto jwtDto1 = createJwtDto(USER_ID, USERNAME, "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(anotherUserId, "anotheruser", "access-2", "refresh-2");
            JwtDto newJwtDto = createJwtDto(USER_ID, USERNAME, "new-access", "new-refresh");

            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // when
            jwtRegistry.rotateJwtInformation("refresh-1", newJwtDto);

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-2")).isTrue();
        }
    }

    @Nested
    @DisplayName("clearExpiredJwtInformation")
    class ClearExpiredJwtInformation {

        @Test
        @DisplayName("만료된 토큰 정리 시 해당 토큰 제거")
        void clearExpiredJwtInformation_removesExpiredTokens() {
            // given
            JwtDto expiredJwtDto = createJwtDto(USER_ID, USERNAME, "expired-access", "expired-refresh");
            jwtRegistry.registerJwtInformation(expiredJwtDto);

            given(tokenProvider.validateRefreshToken("expired-refresh")).willReturn(false);

            // when
            jwtRegistry.clearExpiredJwtInformation();

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("expired-access")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("expired-refresh")).isFalse();
        }

        @Test
        @DisplayName("유효한 토큰은 정리되지 않음")
        void clearExpiredJwtInformation_keepsValidTokens() {
            // given
            JwtDto validJwtDto = createJwtDto(USER_ID, USERNAME, "valid-access", "valid-refresh");
            jwtRegistry.registerJwtInformation(validJwtDto);

            given(tokenProvider.validateRefreshToken("valid-refresh")).willReturn(true);

            // when
            jwtRegistry.clearExpiredJwtInformation();

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByUserId(USER_ID)).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("valid-access")).isTrue();
            assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("valid-refresh")).isTrue();
        }

        @Test
        @DisplayName("일부만 만료된 경우 만료된 것만 제거")
        void clearExpiredJwtInformation_removesOnlyExpired() {
            // given
            JwtDto expiredJwtDto = createJwtDto(USER_ID, USERNAME, "expired-access", "expired-refresh");
            JwtDto validJwtDto = createJwtDto(USER_ID, USERNAME, "valid-access", "valid-refresh");
            jwtRegistry.registerJwtInformation(expiredJwtDto);
            jwtRegistry.registerJwtInformation(validJwtDto);

            given(tokenProvider.validateRefreshToken("expired-refresh")).willReturn(false);
            given(tokenProvider.validateRefreshToken("valid-refresh")).willReturn(true);

            // when
            jwtRegistry.clearExpiredJwtInformation();

            // then
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("expired-access")).isFalse();
            assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("valid-access")).isTrue();
        }
    }

    @Nested
    @DisplayName("getActiveUserIds")
    class GetActiveUserIds {

        @Test
        @DisplayName("활성 사용자 ID 목록 반환")
        void getActiveUserIds_returnsActiveUserIds() {
            // given
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            JwtDto jwtDto1 = createJwtDto(userId1, "user1", "access-1", "refresh-1");
            JwtDto jwtDto2 = createJwtDto(userId2, "user2", "access-2", "refresh-2");

            jwtRegistry.registerJwtInformation(jwtDto1);
            jwtRegistry.registerJwtInformation(jwtDto2);

            // when
            Set<UUID> activeUserIds = jwtRegistry.getActiveUserIds();

            // then
            assertThat(activeUserIds).containsExactlyInAnyOrder(userId1, userId2);
        }

        @Test
        @DisplayName("등록된 JWT가 없으면 빈 Set 반환")
        void getActiveUserIds_noRegistrations_returnsEmptySet() {
            // when
            Set<UUID> activeUserIds = jwtRegistry.getActiveUserIds();

            // then
            assertThat(activeUserIds).isEmpty();
        }
    }
}
