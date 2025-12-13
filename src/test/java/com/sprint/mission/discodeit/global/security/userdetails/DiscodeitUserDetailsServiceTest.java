package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscodeitUserDetailsService 단위 테스트")
class DiscodeitUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsMapper userDetailsMapper;

    @InjectMocks
    private DiscodeitUserDetailsService userDetailsService;

    private static final String ENCODED_PASSWORD = "encodedPassword123456789012345678901234567890123456789012";

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("존재하는 사용자명으로 조회 시 UserDetails 반환")
        void loadUserByUsername_existingUser_returnsUserDetails() {
            // given
            String username = "testuser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "test@example.com", Role.USER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            UserDetails result = userDetailsService.loadUserByUsername(username);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            assertThat(result.getPassword()).isEqualTo(ENCODED_PASSWORD);
            assertThat(result.getAuthorities()).hasSize(1);
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("ADMIN 권한 사용자 조회 시 ROLE_ADMIN 권한 반환")
        void loadUserByUsername_adminUser_returnsAdminAuthority() {
            // given
            String username = "adminuser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "admin@example.com", Role.ADMIN);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.ADMIN);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            UserDetails result = userDetailsService.loadUserByUsername(username);

            // then
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("CHANNEL_MANAGER 권한 사용자 조회 시 ROLE_CHANNEL_MANAGER 권한 반환")
        void loadUserByUsername_channelManager_returnsChannelManagerAuthority() {
            // given
            String username = "manageruser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "manager@example.com", Role.CHANNEL_MANAGER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.CHANNEL_MANAGER);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            UserDetails result = userDetailsService.loadUserByUsername(username);

            // then
            assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_CHANNEL_MANAGER");
        }

        @Test
        @DisplayName("존재하지 않는 사용자명으로 조회 시 UsernameNotFoundException 발생")
        void loadUserByUsername_nonExistingUser_throwsException() {
            // given
            String username = "nonexistent";

            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(username);
        }

        @Test
        @DisplayName("UUID로 조회 시 UserDetails 반환")
        void loadUserByUsername_withUuid_returnsUserDetails() {
            // given
            UUID userId = UUID.randomUUID();
            String username = "testuser";
            User user = createUser(userId, username, "test@example.com", Role.USER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            UserDetails result = userDetailsService.loadUserByUsername(userId.toString());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("존재하지 않는 UUID로 조회 시 UsernameNotFoundException 발생")
        void loadUserByUsername_nonExistingUuid_throwsException() {
            // given
            UUID userId = UUID.randomUUID();

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(userId.toString()))
                .isInstanceOf(UsernameNotFoundException.class);
        }

        @Test
        @DisplayName("사용자 조회 시 UserRepository 호출 검증")
        void loadUserByUsername_callsUserRepository() {
            // given
            String username = "testuser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "test@example.com", Role.USER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            userDetailsService.loadUserByUsername(username);

            // then
            then(userRepository).should().findByUsername(username);
        }

        @Test
        @DisplayName("사용자 조회 시 UserDetailsMapper 호출 검증")
        void loadUserByUsername_callsUserDetailsMapper() {
            // given
            String username = "testuser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "test@example.com", Role.USER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            userDetailsService.loadUserByUsername(username);

            // then
            then(userDetailsMapper).should().toDto(user);
        }

        @Test
        @DisplayName("반환된 UserDetails가 DiscodeitUserDetails 타입")
        void loadUserByUsername_returnsDiscodeitUserDetails() {
            // given
            String username = "testuser";
            UUID userId = UUID.randomUUID();
            User user = createUser(userId, username, "test@example.com", Role.USER);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userId, username, Role.USER);

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(userDetailsMapper.toDto(user)).willReturn(userDetailsDto);

            // when
            UserDetails result = userDetailsService.loadUserByUsername(username);

            // then
            assertThat(result).isInstanceOf(DiscodeitUserDetails.class);
            DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) result;
            assertThat(discodeitUserDetails.getUserDetailsDto()).isEqualTo(userDetailsDto);
        }
    }

    private User createUser(UUID id, String username, String email, Role role) {
        User user = new User(username, email, ENCODED_PASSWORD, null);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "role", role);
        return user;
    }
}
