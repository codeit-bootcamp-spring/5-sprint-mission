package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsMapper 단위 테스트")
class UserDetailsMapperTest {

    private final UserDetailsMapper mapper = new UserDetailsMapper();

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "$2a$10$encrypted";

    @Test
    @DisplayName("User를 UserDetailsDto로 변환 성공")
    void toDto_withValidUser_returnsDto() {
        // given
        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

        // when
        UserDetailsDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_USER_ID);
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
        assertThat(result.role()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void toDto_withNull_returnsNull() {
        // when
        UserDetailsDto result = mapper.toDto(null);

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

        // when
        UserDetailsDto result = mapper.toDto(user);

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

        // when
        UserDetailsDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);
    }

    @Test
    @DisplayName("프로필이 있는 User도 id, username, role만 변환")
    void toDto_withProfile_returnsOnlyIdUsernameRole() {
        // given
        User user = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);

        // when
        UserDetailsDto result = mapper.toDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_USER_ID);
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
        assertThat(result.role()).isEqualTo(Role.USER);
    }
}
