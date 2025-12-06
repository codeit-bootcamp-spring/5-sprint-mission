package com.sprint.mission.discodeit.domain.user.mapper;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
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
    private UserMapper mapper;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "$2a$10$encrypted";

    @Test
    @DisplayName("프로필 없는 User를 UserDto로 변환 성공")
    void toDto_withoutProfile_returnsDto() {
        // given
        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

        given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(true);
        given(binaryContentMapper.toDto(null)).willReturn(null);

        // when
        UserDto result = mapper.toDto(user);

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
    @DisplayName("프로필 있는 User를 UserDto로 변환 성공")
    void toDto_withProfile_returnsDto() {
        // given
        UUID profileId = UUID.randomUUID();
        BinaryContent profile = new BinaryContent("profile.png", 1024L, "image/png");
        ReflectionTestUtils.setField(profile, "id", profileId);

        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, profile);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

        BinaryContentDto profileDto = new BinaryContentDto(
            profileId, "profile.png", 1024L, "image/png", BinaryContentStatus.SUCCESS
        );

        given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(false);
        given(binaryContentMapper.toDto(profile)).willReturn(profileDto);

        // when
        UserDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_USER_ID);
        assertThat(result.profile()).isEqualTo(profileDto);
        assertThat(result.online()).isFalse();
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void toDto_withNull_returnsNull() {
        // when
        UserDto result = mapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("ADMIN 권한의 User 변환 성공")
    void toDto_withAdminRole_returnsDto() {
        // given
        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
        user.updateRole(Role.ADMIN);

        given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(true);
        given(binaryContentMapper.toDto(null)).willReturn(null);

        // when
        UserDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("CHANNEL_MANAGER 권한의 User 변환 성공")
    void toDto_withChannelManagerRole_returnsDto() {
        // given
        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
        user.updateRole(Role.CHANNEL_MANAGER);

        given(jwtRegistry.hasActiveJwtInformationByUserId(TEST_USER_ID)).willReturn(false);
        given(binaryContentMapper.toDto(null)).willReturn(null);

        // when
        UserDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);
    }
}
