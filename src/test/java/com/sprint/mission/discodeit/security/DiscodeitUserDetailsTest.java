package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DiscodeitUserDetails 단위 테스트")
class DiscodeitUserDetailsTest {

    @Test
    @DisplayName("getAuthorities - USER 권한을 ROLE_USER로 반환한다")
    void getAuthorities_UserRole() {
        // given
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true, Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");

        // when
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("getAuthorities - ADMIN 권한을 ROLE_ADMIN으로 반환한다")
    void getAuthorities_AdminRole() {
        // given
        UserDto userDto = new UserDto(UUID.randomUUID(), "admin", "admin@example.com", null, true, Role.ADMIN);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");

        // when
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("getAuthorities - CHANNEL_MANAGER 권한을 ROLE_CHANNEL_MANAGER로 반환한다")
    void getAuthorities_ChannelManagerRole() {
        // given
        UserDto userDto = new UserDto(
            UUID.randomUUID(), "manager", "manager@example.com", null, true, Role.CHANNEL_MANAGER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");

        // when
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_CHANNEL_MANAGER");
    }

    @Test
    @DisplayName("getPassword - 생성 시 전달한 비밀번호를 반환한다")
    void getPassword() {
        // given
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true, Role.USER);
        String expectedPassword = "encodedPassword123";
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, expectedPassword);

        // when
        String password = userDetails.getPassword();

        // then
        assertThat(password).isEqualTo(expectedPassword);
    }

    @Test
    @DisplayName("getUsername - UserDto의 username을 반환한다")
    void getUsername() {
        // given
        String expectedUsername = "testuser";
        UserDto userDto = new UserDto(UUID.randomUUID(), expectedUsername, "test@example.com", null, true, Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");

        // when
        String username = userDetails.getUsername();

        // then
        assertThat(username).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("getUserDto - getter를 통해 UserDto를 반환한다")
    void getUserDto() {
        // given
        UUID userId = UUID.randomUUID();
        UserDto expectedUserDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(expectedUserDto, "password");

        // when
        UserDto userDto = userDetails.getUserDto();

        // then
        assertThat(userDto).isEqualTo(expectedUserDto);
        assertThat(userDto.id()).isEqualTo(userId);
    }

    @Test
    @DisplayName("equals - 같은 UserDto를 가진 객체는 동등하다")
    void equals_SameUserDto() {
        // given
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
        DiscodeitUserDetails userDetails1 = new DiscodeitUserDetails(userDto, "password1");
        DiscodeitUserDetails userDetails2 = new DiscodeitUserDetails(userDto, "password2");

        // when & then
        assertThat(userDetails1).isEqualTo(userDetails2);
    }

    @Test
    @DisplayName("equals - 다른 UserDto를 가진 객체는 동등하지 않다")
    void equals_DifferentUserDto() {
        // given
        UserDto userDto1 = new UserDto(UUID.randomUUID(), "user1", "user1@example.com", null, true, Role.USER);
        UserDto userDto2 = new UserDto(UUID.randomUUID(), "user2", "user2@example.com", null, true, Role.USER);
        DiscodeitUserDetails userDetails1 = new DiscodeitUserDetails(userDto1, "password");
        DiscodeitUserDetails userDetails2 = new DiscodeitUserDetails(userDto2, "password");

        // when & then
        assertThat(userDetails1).isNotEqualTo(userDetails2);
    }
}
