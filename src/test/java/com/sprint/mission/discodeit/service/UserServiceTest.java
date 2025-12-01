package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.UserProfileUploadException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.ENCODED_PASSWORD;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_EMAIL;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_PASSWORD;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_USERNAME;
import static com.sprint.mission.discodeit.support.TestFixtures.createBinaryContentWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MultipartFile profileFile;

    @Mock
    private MultipartFile emptyProfileFile;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("create - 프로필 이미지 없이 사용자 생성 성공")
    void create_WithoutProfile_Success() {
        // given
        UserCreateRequest request = createUserRequest();
        User savedUser = createTestUser();
        UserDto expectedDto = createTestUserDto();

        given(passwordEncoder.encode(TEST_PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
        assertThat(result.email()).isEqualTo(TEST_EMAIL);
        assertThat(result.profile()).isNull();

        then(passwordEncoder).should().encode(TEST_PASSWORD);
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(eventPublisher).should(never()).publishEvent(any(BinaryContentCreatedEvent.class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("create - 프로필 이미지와 함께 사용자 생성 성공")
    void create_WithProfile_Success() throws IOException {
        // given
        UserCreateRequest request = createUserRequest();
        setupProfileFileMock("profile.png", 12345L);

        BinaryContent savedProfile = new BinaryContent("profile.png", 12345L, "image/png");
        User savedUser = new User(TEST_USERNAME, TEST_EMAIL, ENCODED_PASSWORD, savedProfile);
        UserDto expectedDto = createTestUserDto();

        given(passwordEncoder.encode(TEST_PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
            BinaryContent bc = invocation.getArgument(0);
            return createBinaryContentWithId(
                UUID.randomUUID(), bc.getFileName(), bc.getSize(), bc.getContentType()
            );
        });
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, profileFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
        assertThat(result.email()).isEqualTo(TEST_EMAIL);

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(eventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("create - 빈 프로필 이미지로 사용자 생성 시 프로필 저장하지 않음")
    void create_WithEmptyProfile_DoesNotSaveProfile() {
        // given
        UserCreateRequest request = createUserRequest();
        User savedUser = createTestUser();
        UserDto expectedDto = createTestUserDto();

        given(emptyProfileFile.isEmpty()).willReturn(true);
        given(passwordEncoder.encode(TEST_PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, emptyProfileFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.profile()).isNull();

        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(eventPublisher).should(never()).publishEvent(any(BinaryContentCreatedEvent.class));
    }

    @Test
    @DisplayName("create - 프로필 이미지 업로드 실패 시 UserProfileUploadException 발생")
    void create_ProfileUploadFails_ThrowsUserProfileUploadException() throws IOException {
        // given
        UserCreateRequest request = createUserRequest();
        BinaryContent savedProfile = new BinaryContent("profile.png", 12345L, "image/png");

        given(profileFile.isEmpty()).willReturn(false);
        given(profileFile.getOriginalFilename()).willReturn("profile.png");
        given(profileFile.getSize()).willReturn(12345L);
        given(profileFile.getContentType()).willReturn("image/png");
        given(profileFile.getBytes()).willThrow(new IOException("Storage error"));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedProfile);

        // when & then
        assertThatThrownBy(() -> userService.create(request, profileFile))
            .isInstanceOf(UserProfileUploadException.class)
            .hasCauseInstanceOf(IOException.class);

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("create - username과 email을 소문자로 변환하고 공백 제거")
    void create_NormalizesUsernameAndEmail() {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "  TestUser  ", "  Test@Example.COM  ", TEST_PASSWORD
        );
        User savedUser = createTestUser();
        UserDto expectedDto = createTestUserDto();

        given(passwordEncoder.encode(TEST_PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertThat(user.getUsername()).isEqualTo(TEST_USERNAME);
            assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
            return savedUser;
        });
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(TEST_USERNAME);
        assertThat(result.email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("update - 중복된 이메일로 수정 시 DuplicateEmailException 발생")
    void update_DuplicateEmail_ThrowsDuplicateEmailException() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(null, "existing@example.com", null);
        User user = createTestUserWithId(userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("existing@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.update(userId, request, null))
            .isInstanceOf(DuplicateEmailException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("existing@example.com");
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 프로필 이미지와 함께 사용자 수정 성공")
    void update_WithProfile_Success() throws IOException {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(null, null, null);
        User user = createTestUserWithId(userId);
        BinaryContent savedProfile = new BinaryContent("new-profile.png", 54321L, "image/png");
        UserDto expectedDto = createTestUserDto(userId);

        setupProfileFileMock("new-profile.png", 54321L);
        setupCacheMock();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedProfile);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, request, profileFile);

        // then
        assertThat(result).isNotNull();

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(eventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
        then(cacheManager).should().getCache("userDetails");
    }

    @Test
    @DisplayName("update - 새 비밀번호로 수정 시 비밀번호 인코딩")
    void update_WithNewPassword_EncodesPassword() {
        // given
        UUID userId = UUID.randomUUID();
        String oldEncodedPassword = "$2a$10$oldEncodedPassword";
        String newPassword = "newPassword123";
        String newEncodedPassword = "$2a$10$newEncodedPassword";

        UserUpdateRequest request = new UserUpdateRequest(null, null, newPassword);
        User user = new User(TEST_USERNAME, TEST_EMAIL, oldEncodedPassword, null);
        setField(user, "id", userId);
        UserDto expectedDto = createTestUserDto(userId);

        setupCacheMock();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, oldEncodedPassword)).willReturn(false);
        given(passwordEncoder.encode(newPassword)).willReturn(newEncodedPassword);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, request, null);

        // then
        assertThat(result).isNotNull();

        then(passwordEncoder).should().matches(newPassword, oldEncodedPassword);
        then(passwordEncoder).should().encode(newPassword);
    }

    // ========== Helper Methods ==========

    private UserCreateRequest createUserRequest() {
        return new UserCreateRequest("TestUser", "Test@Example.com", TEST_PASSWORD);
    }

    private User createTestUser() {
        return new User(TEST_USERNAME, TEST_EMAIL, ENCODED_PASSWORD, null);
    }

    private User createTestUserWithId(UUID userId) {
        User user = createTestUser();
        setField(user, "id", userId);
        return user;
    }

    private UserDto createTestUserDto() {
        return createUserDto(UUID.randomUUID(), TEST_USERNAME, TEST_EMAIL);
    }

    private UserDto createTestUserDto(UUID userId) {
        return createUserDto(userId, TEST_USERNAME, TEST_EMAIL);
    }

    private void setupProfileFileMock(String filename, long size) throws IOException {
        given(profileFile.isEmpty()).willReturn(false);
        given(profileFile.getOriginalFilename()).willReturn(filename);
        given(profileFile.getSize()).willReturn(size);
        given(profileFile.getContentType()).willReturn("image/png");
        given(profileFile.getBytes()).willReturn("test-image-data".getBytes());
    }

    private void setupCacheMock() {
        Cache mockCache = mock(Cache.class);
        given(cacheManager.getCache("userDetails")).willReturn(mockCache);
    }
}
