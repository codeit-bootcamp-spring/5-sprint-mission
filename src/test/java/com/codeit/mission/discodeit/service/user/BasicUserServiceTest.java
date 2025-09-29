package com.codeit.mission.discodeit.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.UserCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserUpdateRequest;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.UserMapper;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.basic.BasicUserService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @InjectMocks
    private BasicUserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("사용자 생성")
    void create() {
        // given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com",
                "password123");
        User user = new User("testuser", "test@example.com", "password123", null);
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null,
                true);

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto createdUser = userService.create(request, Optional.empty());

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.username()).isEqualTo(request.username());
        then(userRepository).should().existsByEmail(request.email());
        then(userRepository).should().existsByUsername(request.username());
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("사용자 업데이트")
    void update() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("newuser", "new@example.com",
                "newpassword");
        User user = new User("olduser", "old@example.com", "oldpassword", null);
        UserDto userDto = new UserDto(userId, "newuser", "new@example.com", null, false);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);
        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("newuser");
        then(userRepository).should().findById(userId);
    }

    @Test
    @DisplayName("사용자 삭제")
    void delete() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(false);
        willDoNothing().given(userRepository).deleteById(userId);

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().existsById(userId);
        then(userRepository).should().deleteById(userId);
    }
}