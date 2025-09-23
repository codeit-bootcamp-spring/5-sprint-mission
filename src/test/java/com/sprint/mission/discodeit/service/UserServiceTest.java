package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("유저 생성 프로필 미포함 성공")
    public void create_user_success_withoutProfile() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("test")
                .email("test@example.com")
                .password("1234")
                .defaultNickname("testNickname")
                .profileImage(null)
                .build();

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@example.com")
                .password("1234")
                .defaultNickname("TestNickname")
                .build();

        given(userRepository.save(any(User.class))).willReturn(savedUser);
        // when
        UserResponse result = userService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test");
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("유저 생성 프로필 포함 성공")
    void create_success_withProfile() {
        // given
        byte[] imageBytes = "test-image-data".getBytes();
        UserProfileImageRequest profileImageRequest = UserProfileImageRequest.builder()
                .fileName("profile.jpg")
                .contentType("image/jpeg")
                .size((long) imageBytes.length)
                .bytes(imageBytes)
                .build();

        UserCreateRequest request = UserCreateRequest.builder()
                .username("test")
                .email("test@example.com")
                .password("1234")
                .defaultNickname("TestNickname")
                .profileImage(profileImageRequest)
                .build();

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);

        BinaryContent savedProfile = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("profile.jpg")
                .contentType("image/jpeg")
                .size((long) imageBytes.length)
                .build();

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedProfile);
        given(binaryContentStorage.put(any(UUID.class), any(byte[].class)))
                .willReturn(savedProfile.getId());

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .email("test@example.com")
                .password("1234")
                .defaultNickname("TestNickname")
                .profile(savedProfile)
                .build();

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserResponse result = userService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getProfile()).isNotNull();
        assertThat(result.getProfile().getFileName()).isEqualTo("profile.jpg");

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should().existsByEmail("test@example.com");
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(UUID.class), any(byte[].class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("중복된 사용자명으로 유저 생성 실패")
    void create_fail_duplicateUsername() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("test")
                .email("test@example.com")
                .password("1234")
                .defaultNickname("TestNickname")
                .build();

        given(userRepository.existsByUsername("test")).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateUserException.class);

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일으로 유저 생성 실패")
    void create_fail_duplicateEmail() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("test")
                .email("test@example.com")
                .password("password123")
                .defaultNickname("TestNickname")
                .build();

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateUserException.class);

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유저 업데이트 성공")
    void update_success() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .newUsername("newtest")
                .newEmail("newtest@example.com")
                .newPassword("newpassword")
                .build();

        UserProfileImageRequest profileRequest = UserProfileImageRequest.builder()
                .fileName("newprofile.jpg")
                .contentType("image/jpeg")
                .size(1024L)
                .bytes("new-image-data".getBytes())
                .build();

        User existingUser = User.builder()
                .id(userId)
                .username("test")
                .email("test@example.com")
                .password("password")
                .defaultNickname("TestNickname")
                .build();

        BinaryContent newProfile = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("newprofile.jpg")
                .contentType("image/jpeg")
                .size(1024L)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByUsername("newtest")).willReturn(false);
        given(userRepository.existsByEmail("newtest@example.com")).willReturn(false);
        given(binaryContentRepository.saveAndFlush(any(BinaryContent.class))).willReturn(newProfile);
        given(binaryContentStorage.put(any(UUID.class), any(byte[].class))).willReturn(newProfile.getId());
        given(userRepository.save(any(User.class))).willReturn(existingUser);

        // when
        UserResponse result = userService.update(userId, updateRequest, profileRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("newtest");
        then(userRepository).should().existsByEmail("newtest@example.com");
        then(binaryContentRepository).should().saveAndFlush(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(UUID.class), any(byte[].class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저 업데이트 실패")
    void update_fail_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .newUsername("newtest")
                .newEmail("newtest@example.com")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.update(userId, updateRequest, null))
                .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).existsByUsername(any());
        then(userRepository).should(never()).existsByEmail(any());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 사용자명으로 유저 업데이트 실패")
    void update_fail_duplicateUsername() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .newUsername("duplicateuser")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .username("test")
                .email("test@example.com")
                .password("password")
                .defaultNickname("TestNickname")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByUsername("duplicateuser")).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userService.update(userId, updateRequest, null))
                .isInstanceOf(DuplicateUserException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("duplicateuser");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유저 삭제 성공 - ID로 삭제")
    void delete_success_byId() {
        // given
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .username("test")
                .email("test@example.com")
                .password("password")
                .defaultNickname("TestNickname")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        // when
        UserDeleteResponse result = userService.delete(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);

        then(userRepository).should().findById(userId);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 사용자 ID")
    void delete_fail_userNotFound_byId() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).deleteById(userId);
    }
}
