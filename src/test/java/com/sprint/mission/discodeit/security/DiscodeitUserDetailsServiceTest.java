package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscodeitUserDetailsService 단위 테스트")
class DiscodeitUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DiscodeitUserDetailsService userDetailsService;

    private User createUserWithId(UUID id, String username, String email, String password) {
        User user = new User(username, email, password, null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Test
    @DisplayName("loadUserByUsername - 존재하는 사용자를 조회하면 UserDetails를 반환한다")
    void loadUserByUsername_Success() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String password = "encodedPassword123456";
        User user = createUserWithId(userId, username, "test@example.com", password);
        UserDto userDto = new UserDto(userId, username, "test@example.com", null, true, Role.USER);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(result).isInstanceOf(DiscodeitUserDetails.class);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("loadUserByUsername - 존재하지 않는 사용자를 조회하면 UserNotFoundException 발생")
    void loadUserByUsername_UserNotFound() {
        // given
        String username = "nonexistent";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("loadUserByUsername - 반환된 UserDetails에서 권한 정보를 확인할 수 있다")
    void loadUserByUsername_ContainsAuthorities() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "admin";
        User user = createUserWithId(userId, username, "admin@example.com", "password123456");
        UserDto userDto = new UserDto(userId, username, "admin@example.com", null, true, Role.ADMIN);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}
