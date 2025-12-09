package com.sprint.mission.discodeit.user.domain;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 단위 테스트")
class UserTest {

    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "$2a$10$encryptedpassword1234567890";

    private BinaryContent profile;

    @BeforeEach
    void setUp() {
        profile = new BinaryContent("profile.png", 1024L, "image/png");
    }

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 User 생성 성공")
        void constructor_withValidValues_createsUser() {
            // when
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
            assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
            assertThat(user.getProfile()).isEqualTo(profile);
            assertThat(user.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("profile이 null인 경우 User 생성 성공")
        void constructor_withNullProfile_createsUser() {
            // when
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getProfile()).isNull();
        }

        @Test
        @DisplayName("기본 역할은 USER")
        void constructor_defaultRoleIsUser() {
            // when
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // then
            assertThat(user.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("username이 null인 경우 예외 발생")
        void constructor_withNullUsername_throwsException() {
            assertThatThrownBy(() -> new User(null, VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username must not be blank");
        }

        @Test
        @DisplayName("username이 blank인 경우 예외 발생")
        void constructor_withBlankUsername_throwsException() {
            assertThatThrownBy(() -> new User("", VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username must not be blank");
        }

        @Test
        @DisplayName("username이 공백만 있는 경우 예외 발생")
        void constructor_withWhitespaceUsername_throwsException() {
            assertThatThrownBy(() -> new User("   ", VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username must not be blank");
        }

        @Test
        @DisplayName("username이 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongUsername_throwsException() {
            // given
            String longUsername = "a".repeat(User.USERNAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new User(longUsername, VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username must not exceed");
        }

        @Test
        @DisplayName("email이 null인 경우 예외 발생")
        void constructor_withNullEmail_throwsException() {
            assertThatThrownBy(() -> new User(VALID_USERNAME, null, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email must not be blank");
        }

        @Test
        @DisplayName("email이 blank인 경우 예외 발생")
        void constructor_withBlankEmail_throwsException() {
            assertThatThrownBy(() -> new User(VALID_USERNAME, "", VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email must not be blank");
        }

        @Test
        @DisplayName("email이 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongEmail_throwsException() {
            // given
            String longEmail = "a".repeat(User.EMAIL_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new User(VALID_USERNAME, longEmail, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email must not exceed");
        }

        @Test
        @DisplayName("password가 null인 경우 예외 발생")
        void constructor_withNullPassword_throwsException() {
            assertThatThrownBy(() -> new User(VALID_USERNAME, VALID_EMAIL, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password must not be blank");
        }

        @Test
        @DisplayName("password가 blank인 경우 예외 발생")
        void constructor_withBlankPassword_throwsException() {
            assertThatThrownBy(() -> new User(VALID_USERNAME, VALID_EMAIL, "", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password must not be blank");
        }

        @Test
        @DisplayName("password가 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongPassword_throwsException() {
            // given
            String longPassword = "a".repeat(User.PASSWORD_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new User(VALID_USERNAME, VALID_EMAIL, longPassword, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("encoded password must not exceed");
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("모든 필드 변경 성공")
        void update_withAllFields_updatesUser() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            BinaryContent newProfile = new BinaryContent("new-profile.png", 2048L, "image/png");

            // when
            user.update("newuser", "new@example.com", "newpassword123456789012345678901", newProfile);

            // then
            assertThat(user.getUsername()).isEqualTo("newuser");
            assertThat(user.getEmail()).isEqualTo("new@example.com");
            assertThat(user.getPassword()).isEqualTo("newpassword123456789012345678901");
            assertThat(user.getProfile()).isEqualTo(newProfile);
        }

        @Test
        @DisplayName("username만 변경 성공")
        void update_withOnlyUsername_updatesUsername() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.update("newuser", null, null, null);

            // then
            assertThat(user.getUsername()).isEqualTo("newuser");
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
            assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
            assertThat(user.getProfile()).isEqualTo(profile);
        }

        @Test
        @DisplayName("email만 변경 성공")
        void update_withOnlyEmail_updatesEmail() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.update(null, "new@example.com", null, null);

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("password만 변경 성공")
        void update_withOnlyPassword_updatesPassword() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            user.update(null, null, "newpassword123456789012345678901", null);

            // then
            assertThat(user.getPassword()).isEqualTo("newpassword123456789012345678901");
        }

        @Test
        @DisplayName("profile만 변경 성공")
        void update_withOnlyProfile_updatesProfile() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            BinaryContent newProfile = new BinaryContent("new-profile.png", 2048L, "image/png");

            // when
            user.update(null, null, null, newProfile);

            // then
            assertThat(user.getProfile()).isEqualTo(newProfile);
        }

        @Test
        @DisplayName("모든 파라미터가 null인 경우 기존 값 유지")
        void update_withAllNull_keepsOriginalValues() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.update(null, null, null, null);

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
            assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
            assertThat(user.getProfile()).isEqualTo(profile);
        }

        @Test
        @DisplayName("자기 자신 반환 (fluent API)")
        void update_returnsItself() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            User result = user.update("newuser", null, null, null);

            // then
            assertThat(result).isSameAs(user);
        }

        @Test
        @DisplayName("username이 최대 길이 초과 시 예외 발생")
        void update_withTooLongUsername_throwsException() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longUsername = "a".repeat(User.USERNAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> user.update(longUsername, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username must not exceed");
        }

        @Test
        @DisplayName("email이 최대 길이 초과 시 예외 발생")
        void update_withTooLongEmail_throwsException() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longEmail = "a".repeat(User.EMAIL_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> user.update(null, longEmail, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email must not exceed");
        }

        @Test
        @DisplayName("password가 최대 길이 초과 시 예외 발생")
        void update_withTooLongPassword_throwsException() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longPassword = "a".repeat(User.PASSWORD_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> user.update(null, null, longPassword, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("encoded password must not exceed");
        }
    }

    @Nested
    @DisplayName("updateRole 메서드")
    class UpdateRoleTest {

        @Test
        @DisplayName("ADMIN 역할로 변경 성공")
        void updateRole_toAdmin_updatesRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            user.updateRole(Role.ADMIN);

            // then
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("CHANNEL_MANAGER 역할로 변경 성공")
        void updateRole_toChannelManager_updatesRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            user.updateRole(Role.CHANNEL_MANAGER);

            // then
            assertThat(user.getRole()).isEqualTo(Role.CHANNEL_MANAGER);
        }

        @Test
        @DisplayName("USER 역할로 변경 성공")
        void updateRole_toUser_updatesRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            user.updateRole(Role.ADMIN);

            // when
            user.updateRole(Role.USER);

            // then
            assertThat(user.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("null인 경우 기존 역할 유지")
        void updateRole_withNull_keepsOriginalRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            user.updateRole(Role.ADMIN);

            // when
            user.updateRole(null);

            // then
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("자기 자신 반환 (fluent API)")
        void updateRole_returnsItself() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            User result = user.updateRole(Role.ADMIN);

            // then
            assertThat(result).isSameAs(user);
        }

        @Test
        @DisplayName("동일한 역할로 변경해도 정상 동작")
        void updateRole_withSameRole_updatesRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            user.updateRole(Role.USER);

            // then
            assertThat(user.getRole()).isEqualTo(Role.USER);
        }
    }
}
