package com.sprint.mission.discodeit.user.domain;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
        @DisplayName("유효한 값 입력 시 User 생성 성공")
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
        @DisplayName("profile이 null인 경우에도 User 생성 성공")
        void constructor_withNullProfile_createsUser() {
            // when
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // then
            assertThat(user.getProfile()).isNull();
            assertThat(user.getRole()).isEqualTo(Role.USER);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("username이 null, 빈 문자열, 공백 시 예외 발생")
        void constructor_withInvalidUsername_throwsException(String invalidUsername) {
            assertThatThrownBy(() -> new User(invalidUsername, VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("username 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongUsername_throwsException() {
            String longUsername = "a".repeat(User.USERNAME_MAX_LENGTH + 1);

            assertThatThrownBy(() -> new User(longUsername, VALID_EMAIL, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("email이 null, 빈 문자열, 공백 시 예외 발생")
        void constructor_withInvalidEmail_throwsException(String invalidEmail) {
            assertThatThrownBy(() -> new User(VALID_USERNAME, invalidEmail, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("email 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongEmail_throwsException() {
            String longEmail = "a".repeat(User.EMAIL_MAX_LENGTH + 1);

            assertThatThrownBy(() -> new User(VALID_USERNAME, longEmail, VALID_PASSWORD, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("password가 null, 빈 문자열, 공백 시 예외 발생")
        void constructor_withInvalidPassword_throwsException(String invalidPassword) {
            assertThatThrownBy(() -> new User(VALID_USERNAME, VALID_EMAIL, invalidPassword, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("password 최대 길이 초과 시 예외 발생")
        void constructor_withTooLongPassword_throwsException() {
            String longPassword = "a".repeat(User.ENCODED_PASSWORD_MAX_LENGTH + 1);

            assertThatThrownBy(() -> new User(VALID_USERNAME, VALID_EMAIL, longPassword, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("updateInfo 메서드")
    class UpdateInfoTest {

        @Test
        @DisplayName("username과 email 모두 입력 시 변경 성공")
        void updateInfo_withBothFields_updatesUser() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            User result = user.updateInfo("newuser", "new@example.com");

            // then
            assertThat(result).isSameAs(user);
            assertThat(user.getUsername()).isEqualTo("newuser");
            assertThat(user.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("username만 입력 시 email은 기존 값 유지")
        void updateInfo_withOnlyUsername_updatesUsername() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.updateInfo("newuser", null);

            // then
            assertThat(user.getUsername()).isEqualTo("newuser");
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        }

        @Test
        @DisplayName("email만 입력 시 username은 기존 값 유지")
        void updateInfo_withOnlyEmail_updatesEmail() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.updateInfo(null, "new@example.com");

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("모든 파라미터 null 시 기존 값 유지")
        void updateInfo_withAllNull_keepsOriginalValues() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.updateInfo(null, null);

            // then
            assertThat(user.getUsername()).isEqualTo(VALID_USERNAME);
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        }

        @Test
        @DisplayName("username 최대 길이 초과 시 예외 발생")
        void updateInfo_withTooLongUsername_throwsException() {
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longUsername = "a".repeat(User.USERNAME_MAX_LENGTH + 1);

            assertThatThrownBy(() -> user.updateInfo(longUsername, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("email 최대 길이 초과 시 예외 발생")
        void updateInfo_withTooLongEmail_throwsException() {
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longEmail = "a".repeat(User.EMAIL_MAX_LENGTH + 1);

            assertThatThrownBy(() -> user.updateInfo(null, longEmail))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("updatePassword 메서드")
    class UpdatePasswordTest {

        @Test
        @DisplayName("유효한 password 입력 시 변경 성공 및 자기 자신 반환")
        void updatePassword_withValidPassword_updatesAndReturnsItself() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String newPassword = "$2a$10$newencryptedpassword12345";

            // when
            User result = user.updatePassword(newPassword);

            // then
            assertThat(result).isSameAs(user);
            assertThat(user.getPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("password null 입력 시 기존 값 유지")
        void updatePassword_withNull_keepsOriginalPassword() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            user.updatePassword(null);

            // then
            assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
        }

        @Test
        @DisplayName("password 최대 길이 초과 시 예외 발생")
        void updatePassword_withTooLongPassword_throwsException() {
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            String longPassword = "a".repeat(User.ENCODED_PASSWORD_MAX_LENGTH + 1);

            assertThatThrownBy(() -> user.updatePassword(longPassword))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("updateProfile 메서드")
    class UpdateProfileTest {

        @Test
        @DisplayName("유효한 profile 입력 시 변경 성공 및 자기 자신 반환")
        void updateProfile_withValidProfile_updatesAndReturnsItself() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);
            BinaryContent newProfile = new BinaryContent("new-profile.png", 2048L, "image/png");

            // when
            User result = user.updateProfile(newProfile);

            // then
            assertThat(result).isSameAs(user);
            assertThat(user.getProfile()).isSameAs(newProfile);
            assertThat(user.getProfile()).isNotSameAs(profile);
        }

        @Test
        @DisplayName("profile null 입력 시 기존 값 유지")
        void updateProfile_withNull_keepsOriginalProfile() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, profile);

            // when
            user.updateProfile(null);

            // then
            assertThat(user.getProfile()).isEqualTo(profile);
        }
    }

    @Nested
    @DisplayName("updateRole 메서드")
    class UpdateRoleTest {

        @ParameterizedTest
        @EnumSource(Role.class)
        @DisplayName("유효한 역할 입력 시 변경 성공")
        void updateRole_withValidRole_updatesRole(Role role) {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);

            // when
            User result = user.updateRole(role);

            // then
            assertThat(result).isSameAs(user);
            assertThat(user.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("role null 입력 시 기존 값 유지")
        void updateRole_withNull_keepsOriginalRole() {
            // given
            User user = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, null);
            user.updateRole(Role.ADMIN);

            // when
            user.updateRole(null);

            // then
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        }
    }
}
