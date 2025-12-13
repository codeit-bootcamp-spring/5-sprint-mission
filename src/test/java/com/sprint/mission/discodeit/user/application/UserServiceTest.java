package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateUsernameException;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProfileImageManager profileImageManager;

    @Mock
    private CacheService cacheService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String USER_AGENT = "TestAgent";

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("유효한 요청 시 사용자 생성 성공")
        void create_withValidRequest_createsUser() {
            // given
            UserCreateRequest request = new UserCreateRequest("TestUser", "test@example.com", "password123");
            User savedUser = createMockUser(UUID.randomUUID(), "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(savedUser.getId(), "testuser", "test@example.com");

            given(userRepository.existsByUsername("testuser")).willReturn(false);
            given(userRepository.existsByEmail("test@example.com")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
            given(profileImageManager.save(null)).willReturn(null);
            given(userRepository.save(any(User.class))).willReturn(savedUser);
            given(userMapper.toDto(savedUser)).willReturn(expectedDto);

            // when
            UserDto result = userService.create(request, null);

            // then
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.email()).isEqualTo("test@example.com");
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("프로필 이미지 포함 시 사용자 생성 성공")
        void create_withProfile_createsUserWithProfile() {
            // given
            UserCreateRequest request = new UserCreateRequest("TestUser", "test@example.com", "password123");
            MockMultipartFile profile = new MockMultipartFile(
                "profile", "profile.png", "image/png", "image".getBytes());
            BinaryContent savedProfile = new BinaryContent("profile.png", 5L, "image/png");
            User savedUser = createMockUser(UUID.randomUUID(), "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(savedUser.getId(), "testuser", "test@example.com");

            given(userRepository.existsByUsername("testuser")).willReturn(false);
            given(userRepository.existsByEmail("test@example.com")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
            given(profileImageManager.save(profile)).willReturn(savedProfile);
            given(userRepository.save(any(User.class))).willReturn(savedUser);
            given(userMapper.toDto(savedUser)).willReturn(expectedDto);

            // when
            UserDto result = userService.create(request, profile);

            // then
            assertThat(result).isNotNull();
            then(profileImageManager).should().save(profile);
        }

        @Test
        @DisplayName("username 입력 시 소문자로 정규화하여 저장")
        void create_normalizesUsername() {
            // given
            UserCreateRequest request = new UserCreateRequest("  TestUser  ", "test@example.com", "password123");
            User savedUser = createMockUser(UUID.randomUUID(), "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(savedUser.getId(), "testuser", "test@example.com");

            given(userRepository.existsByUsername("testuser")).willReturn(false);
            given(userRepository.existsByEmail("test@example.com")).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);
            given(userMapper.toDto(savedUser)).willReturn(expectedDto);

            // when
            userService.create(request, null);

            // then
            then(userRepository).should().existsByUsername("testuser");
        }

        @Test
        @DisplayName("중복된 username 시 DuplicateUsernameException 발생")
        void create_withDuplicateUsername_throwsException() {
            // given
            UserCreateRequest request = new UserCreateRequest("existing", "new@example.com", "password123");
            given(userRepository.existsByUsername("existing")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(DuplicateUsernameException.class);

            then(userRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("중복된 email 시 DuplicateEmailException 발생")
        void create_withDuplicateEmail_throwsException() {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "existing@example.com", "password123");
            given(userRepository.existsByUsername("newuser")).willReturn(false);
            given(userRepository.existsByEmail("existing@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(DuplicateEmailException.class);

            then(userRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTest {

        @Test
        @DisplayName("전체 조회 시 모든 사용자 반환")
        void findAll_returnsAllUsers() {
            // given
            User user1 = createMockUser(UUID.randomUUID(), "user1", "user1@example.com");
            User user2 = createMockUser(UUID.randomUUID(), "user2", "user2@example.com");
            List<User> users = List.of(user1, user2);
            List<UserDto> expectedDtos = List.of(
                createUserDto(user1.getId(), "user1", "user1@example.com"),
                createUserDto(user2.getId(), "user2", "user2@example.com")
            );

            given(userRepository.findAllWithProfile()).willReturn(users);
            given(userMapper.toDtoList(users)).willReturn(expectedDtos);

            // when
            List<UserDto> result = userService.findAll();

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("사용자 없음 시 빈 리스트 반환")
        void findAll_withNoUsers_returnsEmptyList() {
            // given
            given(userRepository.findAllWithProfile()).willReturn(List.of());
            given(userMapper.toDtoList(List.of())).willReturn(List.of());

            // when
            List<UserDto> result = userService.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID 조회 시 사용자 반환")
        void findById_withExistingUser_returnsUser() {
            // given
            UUID userId = UUID.randomUUID();
            User user = createMockUser(userId, "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            UserDto result = userService.findById(userId);

            // then
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.username()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 UserNotFoundException 발생")
        void findById_withNonExistingUser_throwsException() {
            // given
            UUID userId = UUID.randomUUID();
            given(userRepository.findWithProfileById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청 시 사용자 정보 수정 성공")
        void update_withValidRequest_updatesUser() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("newuser", "new@example.com", null);
            User user = createMockUser(userId, "olduser", "old@example.com");
            UserDto expectedDto = createUserDto(userId, "newuser", "new@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByUsername("newuser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            UserDto result = userService.update(userId, request, null, IP_ADDRESS, USER_AGENT);

            // then
            assertThat(result.username()).isEqualTo("newuser");
        }

        @Test
        @DisplayName("프로필 이미지 변경 시 기존 이미지 삭제")
        void update_withNewProfile_deletesOldProfile() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, null, null);
            MockMultipartFile newProfile = new MockMultipartFile(
                "profile", "new.png", "image/png", "image".getBytes());
            BinaryContent oldProfile = new BinaryContent("old.png", 100L, "image/png");
            BinaryContent savedProfile = new BinaryContent("new.png", 5L, "image/png");
            User user = createMockUserWithProfile(userId, oldProfile);
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(profileImageManager.save(newProfile)).willReturn(savedProfile);
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, newProfile, IP_ADDRESS, USER_AGENT);

            // then
            then(profileImageManager).should().save(newProfile);
            then(profileImageManager).should().delete(oldProfile);
        }

        @Test
        @DisplayName("비밀번호 변경 시 CredentialUpdatedEvent 발행")
        void update_withNewPassword_publishesEvent() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, null, "newPassword");
            User user = createMockUser(userId, "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("newPassword", user.getPassword())).willReturn(false);
            given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, null, IP_ADDRESS, USER_AGENT);

            // then
            ArgumentCaptor<CredentialUpdatedEvent> captor = ArgumentCaptor.forClass(CredentialUpdatedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());

            CredentialUpdatedEvent event = captor.getValue();
            assertThat(event.userId()).isEqualTo(userId);
            assertThat(event.ipAddress()).isEqualTo(IP_ADDRESS);
        }

        @Test
        @DisplayName("동일한 비밀번호 시 이벤트 발행하지 않음")
        void update_withSamePassword_doesNotPublishEvent() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, null, "samePassword");
            User user = createMockUser(userId, "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("samePassword", user.getPassword())).willReturn(true);
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, null, IP_ADDRESS, USER_AGENT);

            // then
            then(eventPublisher).should(never()).publishEvent(any(CredentialUpdatedEvent.class));
        }

        @Test
        @DisplayName("동일한 username으로 변경 시 중복 검사 생략")
        void update_withSameUsername_skipsValidation() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("testuser", null, null);
            User user = createMockUser(userId, "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, null, IP_ADDRESS, USER_AGENT);

            // then
            then(userRepository).should(never()).existsByUsername(anyString());
        }

        @Test
        @DisplayName("동일한 email로 변경 시 중복 검사 생략")
        void update_withSameEmail_skipsValidation() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, "test@example.com", null);
            User user = createMockUser(userId, "testuser", "test@example.com");
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, null, IP_ADDRESS, USER_AGENT);

            // then
            then(userRepository).should(never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("중복된 username으로 변경 시 DuplicateUsernameException 발생")
        void update_withDuplicateUsername_throwsException() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("existinguser", null, null);
            User user = createMockUser(userId, "olduser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByUsername("existinguser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.update(userId, request, null, IP_ADDRESS, USER_AGENT))
                .isInstanceOf(DuplicateUsernameException.class);
        }

        @Test
        @DisplayName("중복된 email로 변경 시 DuplicateEmailException 발생")
        void update_withDuplicateEmail_throwsException() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, "existing@example.com", null);
            User user = createMockUser(userId, "testuser", "old@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail("existing@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.update(userId, request, null, IP_ADDRESS, USER_AGENT))
                .isInstanceOf(DuplicateEmailException.class);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시 UserNotFoundException 발생")
        void update_withNonExistingUser_throwsException() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("newuser", null, null);
            given(userRepository.findWithProfileById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.update(userId, request, null, IP_ADDRESS, USER_AGENT))
                .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("빈 프로필 이미지 전달 시 프로필 업데이트 생략")
        void update_withEmptyProfile_skipsProfileUpdate() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, null, null);
            MockMultipartFile emptyProfile = new MockMultipartFile(
                "profile", "empty.png", "image/png", new byte[0]);
            BinaryContent existingProfile = new BinaryContent("existing.png", 100L, "image/png");
            User user = createMockUserWithProfile(userId, existingProfile);
            UserDto expectedDto = createUserDto(userId, "testuser", "test@example.com");

            given(userRepository.findWithProfileById(userId)).willReturn(Optional.of(user));
            given(userMapper.toDto(user)).willReturn(expectedDto);

            // when
            userService.update(userId, request, emptyProfile, IP_ADDRESS, USER_AGENT);

            // then
            then(profileImageManager).should(never()).save(any());
            then(profileImageManager).should(never()).delete(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTest {

        @Test
        @DisplayName("존재하는 사용자 삭제 시 UserDeletedEvent 발행")
        void deleteById_withExistingUser_deletesAndPublishesEvent() {
            // given
            UUID userId = UUID.randomUUID();
            User user = createMockUser(userId, "testuser", "test@example.com");

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            userService.deleteById(userId);

            // then
            then(profileImageManager).should().delete(user.getProfile());
            then(userRepository).should().delete(user);

            ArgumentCaptor<UserDeletedEvent> captor = ArgumentCaptor.forClass(UserDeletedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());
            assertThat(captor.getValue().userId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("프로필 이미지 있는 사용자 삭제 시 프로필도 삭제")
        void deleteById_withProfile_deletesProfile() {
            // given
            UUID userId = UUID.randomUUID();
            BinaryContent profile = new BinaryContent("profile.png", 100L, "image/png");
            User user = createMockUserWithProfile(userId, profile);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            userService.deleteById(userId);

            // then
            then(profileImageManager).should().delete(profile);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 시 UserNotFoundException 발생")
        void deleteById_withNonExistingUser_throwsException() {
            // given
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.deleteById(userId))
                .isInstanceOf(UserNotFoundException.class);

            then(userRepository).should(never()).delete(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }
    }

    private User createMockUser(UUID id, String username, String email) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getUsername()).thenReturn(username);
        lenient().when(user.getEmail()).thenReturn(email);
        lenient().when(user.getPassword()).thenReturn("encodedPassword");
        lenient().when(user.getProfile()).thenReturn(null);
        return user;
    }

    private User createMockUserWithProfile(UUID id, BinaryContent profile) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getUsername()).thenReturn("testuser");
        lenient().when(user.getEmail()).thenReturn("test@example.com");
        lenient().when(user.getPassword()).thenReturn("encodedPassword");
        lenient().when(user.getProfile()).thenReturn(profile);
        return user;
    }

    private UserDto createUserDto(UUID id, String username, String email) {
        return new UserDto(id, username, email, null, true, Role.USER);
    }
}
