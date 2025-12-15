package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper 단위 테스트")
class UserMapperTest {

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private UserMapper userMapper;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_PROFILE_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "$2a$10$encryptedpassword";

    private User testUser;
    private BinaryContent testProfile;

    @BeforeEach
    void setUp() {
        testProfile = new BinaryContent("profile.png", 1024L, "image/png");
        ReflectionTestUtils.setField(testProfile, "id", TEST_PROFILE_ID);
        ReflectionTestUtils.setField(testProfile, "status", BinaryContentStatus.SUCCESS);

        testUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(testUser, "id", TEST_USER_ID);
    }

    @Nested
    @DisplayName("toDto 메서드")
    class ToDto {

        @Test
        @DisplayName("User를 UserDto로 변환 성공 (온라인 상태)")
        void toDto_withOnlineUser_returnsDto() {
            // given
            given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(true);
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            UserDto result = userMapper.toDto(testUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_USER_ID);
            assertThat(result.username()).isEqualTo(TEST_USERNAME);
            assertThat(result.email()).isEqualTo(TEST_EMAIL);
            assertThat(result.profile()).isNull();
            assertThat(result.online()).isTrue();
            assertThat(result.role()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("User를 UserDto로 변환 성공 (오프라인 상태)")
        void toDto_withOfflineUser_returnsDto() {
            // given
            given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(false);
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            UserDto result = userMapper.toDto(testUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.online()).isFalse();
        }

        @Test
        @DisplayName("프로필이 있는 User 변환 성공")
        void toDto_withProfile_returnsDto() {
            // given
            User userWithProfile = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, testProfile);
            ReflectionTestUtils.setField(userWithProfile, "id", TEST_USER_ID);

            BinaryContentDto profileDto = new BinaryContentDto(
                TEST_PROFILE_ID, "profile.png", 1024L, "image/png", BinaryContentStatus.SUCCESS);

            given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(true);
            given(binaryContentMapper.toDto(testProfile)).willReturn(profileDto);

            // when
            UserDto result = userMapper.toDto(userWithProfile);

            // then
            assertThat(result).isNotNull();
            assertThat(result.profile()).isNotNull();
            assertThat(result.profile().id()).isEqualTo(TEST_PROFILE_ID);
            assertThat(result.profile().fileName()).isEqualTo("profile.png");
        }

        @Test
        @DisplayName("null 입력 시 null 반환")
        void toDto_withNull_returnsNull() {
            // when
            UserDto result = userMapper.toDto(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("ADMIN 역할 User 변환 성공")
        void toDto_withAdminRole_returnsDto() {
            // given
            testUser.updateRole(Role.ADMIN);

            given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(true);
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            UserDto result = userMapper.toDto(testUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.role()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("CHANNEL_MANAGER 역할 User 변환 성공")
        void toDto_withChannelManagerRole_returnsDto() {
            // given
            testUser.updateRole(Role.CHANNEL_MANAGER);

            given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(false);
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            UserDto result = userMapper.toDto(testUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);
        }
    }

    @Nested
    @DisplayName("toDtoList 메서드")
    class ToDtoList {

        @Test
        @DisplayName("User 목록을 UserDto 목록으로 변환 성공")
        void toDtoList_withValidList_returnsDtoList() {
            // given
            UUID user2Id = UUID.randomUUID();
            User user2 = new User("user2", "user2@example.com", TEST_PASSWORD, null);
            ReflectionTestUtils.setField(user2, "id", user2Id);

            List<User> users = List.of(testUser, user2);

            given(jwtRegistry.getActiveUserIds()).willReturn(Set.of(TEST_USER_ID));
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            List<UserDto> result = userMapper.toDtoList(users);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(TEST_USER_ID);
            assertThat(result.get(0).online()).isTrue();
            assertThat(result.get(1).id()).isEqualTo(user2Id);
            assertThat(result.get(1).online()).isFalse();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환")
        void toDtoList_withNull_returnsEmptyList() {
            // when
            List<UserDto> result = userMapper.toDtoList(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void toDtoList_withEmptyList_returnsEmptyList() {
            // when
            List<UserDto> result = userMapper.toDtoList(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("모든 사용자가 온라인인 경우")
        void toDtoList_withAllOnlineUsers_returnsDtoListWithAllOnline() {
            // given
            UUID user2Id = UUID.randomUUID();
            User user2 = new User("user2", "user2@example.com", TEST_PASSWORD, null);
            ReflectionTestUtils.setField(user2, "id", user2Id);

            List<User> users = List.of(testUser, user2);

            given(jwtRegistry.getActiveUserIds()).willReturn(Set.of(TEST_USER_ID, user2Id));
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            List<UserDto> result = userMapper.toDtoList(users);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(UserDto::online);
        }

        @Test
        @DisplayName("모든 사용자가 오프라인인 경우")
        void toDtoList_withAllOfflineUsers_returnsDtoListWithAllOffline() {
            // given
            UUID user2Id = UUID.randomUUID();
            User user2 = new User("user2", "user2@example.com", TEST_PASSWORD, null);
            ReflectionTestUtils.setField(user2, "id", user2Id);

            List<User> users = List.of(testUser, user2);

            given(jwtRegistry.getActiveUserIds()).willReturn(Set.of());
            given(binaryContentMapper.toDto(null)).willReturn(null);

            // when
            List<UserDto> result = userMapper.toDtoList(users);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).noneMatch(UserDto::online);
        }

        @Test
        @DisplayName("프로필이 있는 사용자 목록 변환 성공")
        void toDtoList_withUsersHavingProfile_returnsDtoList() {
            // given
            User userWithProfile = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, testProfile);
            ReflectionTestUtils.setField(userWithProfile, "id", TEST_USER_ID);

            BinaryContentDto profileDto = new BinaryContentDto(
                TEST_PROFILE_ID, "profile.png", 1024L, "image/png", BinaryContentStatus.SUCCESS);

            List<User> users = List.of(userWithProfile);

            given(jwtRegistry.getActiveUserIds()).willReturn(Set.of(TEST_USER_ID));
            given(binaryContentMapper.toDto(testProfile)).willReturn(profileDto);

            // when
            List<UserDto> result = userMapper.toDtoList(users);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).profile()).isNotNull();
            assertThat(result.get(0).profile().fileName()).isEqualTo("profile.png");
        }

    }
}
