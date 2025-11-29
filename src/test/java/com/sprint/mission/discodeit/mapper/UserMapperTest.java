package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    private User createUserWithId(UUID id, String username, String email, BinaryContent profile) {
        User user = new User(username, email, "encodedPassword123456", profile);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private BinaryContent createBinaryContentWithId(UUID id) {
        BinaryContent content = new BinaryContent("profile.png", 1024L, "image/png");
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

    @Test
    @DisplayName("온라인 사용자를 DTO로 변환한다")
    void toDto_OnlineUser() {
        // given
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        BinaryContent profile = createBinaryContentWithId(profileId);
        User user = createUserWithId(userId, "testuser", "test@example.com", profile);
        BinaryContentDto profileDto = new BinaryContentDto(profileId, "profile.png", 1024L, "image/png");

        given(jwtRegistry.hasActiveJwtInformationByUserId(userId)).willReturn(true);
        given(binaryContentMapper.toDto(profile)).willReturn(profileDto);

        // when
        UserDto result = userMapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.online()).isTrue();
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().id()).isEqualTo(profileId);
    }

    @Test
    @DisplayName("오프라인 사용자를 DTO로 변환한다")
    void toDto_OfflineUser() {
        // given
        UUID userId = UUID.randomUUID();
        User user = createUserWithId(userId, "offlineuser", "offline@example.com", null);

        given(jwtRegistry.hasActiveJwtInformationByUserId(userId)).willReturn(false);

        // when
        UserDto result = userMapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("offlineuser");
        assertThat(result.email()).isEqualTo("offline@example.com");
        assertThat(result.online()).isFalse();
        assertThat(result.profile()).isNull();
    }

    @Test
    @DisplayName("프로필이 없는 사용자를 DTO로 변환한다")
    void toDto_UserWithoutProfile() {
        // given
        UUID userId = UUID.randomUUID();
        User user = createUserWithId(userId, "noprofile", "noprofile@example.com", null);

        given(jwtRegistry.hasActiveJwtInformationByUserId(userId)).willReturn(true);

        // when
        UserDto result = userMapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.profile()).isNull();
        assertThat(result.online()).isTrue();
    }

    @Test
    @DisplayName("프로필이 있는 사용자를 DTO로 변환하면 프로필 정보가 포함된다")
    void toDto_UserWithProfile() {
        // given
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        BinaryContent profile = createBinaryContentWithId(profileId);
        User user = createUserWithId(userId, "withprofile", "profile@example.com", profile);
        BinaryContentDto profileDto = new BinaryContentDto(profileId, "profile.png", 1024L, "image/png");

        given(jwtRegistry.hasActiveJwtInformationByUserId(userId)).willReturn(false);
        given(binaryContentMapper.toDto(profile)).willReturn(profileDto);

        // when
        UserDto result = userMapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().id()).isEqualTo(profileId);
        assertThat(result.profile().fileName()).isEqualTo("profile.png");
        assertThat(result.profile().size()).isEqualTo(1024L);
        assertThat(result.profile().contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("null 사용자를 변환하면 null을 반환한다")
    void toDto_NullUser() {
        // when
        UserDto result = userMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }
}
