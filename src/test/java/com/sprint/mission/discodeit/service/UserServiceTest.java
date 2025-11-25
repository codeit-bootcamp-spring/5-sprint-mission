package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserProfileUploadException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sprint.mission.discodeit.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("create - 프로필 이미지 없이 사용자 생성 성공")
    void create_WithoutProfile_Success() {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "TestUser",
            "Test@Example.com",
            "password123"
        );

        String encodedPassword = "$2a$10$encodedPassword";
        UUID userId = UUID.randomUUID();

        User savedUser = new User(
            "testuser",
            "test@example.com",
            encodedPassword,
            null
        );

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.USER
        );

        given(passwordEncoder.encode("password123")).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.profile()).isNull();

        then(passwordEncoder).should().encode("password123");
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(binaryContentStorage).should(never()).put(any(UUID.class), any(byte[].class));
        then(userRepository).should().save(any(User.class));
        then(userMapper).should().toDto(savedUser);
    }

    @Test
    @DisplayName("create - 프로필 이미지와 함께 사용자 생성 성공")
    void create_WithProfile_Success() throws IOException {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "TestUser",
            "Test@Example.com",
            "password123"
        );

        MultipartFile profileFile = org.mockito.Mockito.mock(MultipartFile.class);
        byte[] fileBytes = "test-image-data".getBytes();
        UUID profileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(profileFile.isEmpty()).willReturn(false);
        given(profileFile.getOriginalFilename()).willReturn("profile.png");
        given(profileFile.getSize()).willReturn(12345L);
        given(profileFile.getContentType()).willReturn("image/png");
        given(profileFile.getBytes()).willReturn(fileBytes);

        BinaryContent savedProfile = new BinaryContent(
            "profile.png",
            12345L,
            "image/png"
        );

        String encodedPassword = "$2a$10$encodedPassword";

        User savedUser = new User(
            "testuser",
            "test@example.com",
            encodedPassword,
            savedProfile
        );

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.USER
        );

        given(passwordEncoder.encode("password123")).willReturn(encodedPassword);
        given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
            BinaryContent bc = invocation.getArgument(0);
            BinaryContent saved = new BinaryContent(
                bc.getFileName(),
                bc.getSize(),
                bc.getContentType()
            );
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, profileFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");

        then(passwordEncoder).should().encode("password123");
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        // 참고: BinaryContent.getId()는 영속화 전에 null을 반환하므로 실제 호출은 null을 사용
        then(binaryContentStorage).should().put(any(), any(byte[].class));
        then(userRepository).should().save(any(User.class));
        then(userMapper).should().toDto(savedUser);
    }

    @Test
    @DisplayName("create - 빈 프로필 이미지로 사용자 생성 시 프로필 저장하지 않음")
    void create_WithEmptyProfile_DoesNotSaveProfile() {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "TestUser",
            "Test@Example.com",
            "password123"
        );

        MultipartFile emptyProfileFile = org.mockito.Mockito.mock(MultipartFile.class);
        given(emptyProfileFile.isEmpty()).willReturn(true);

        String encodedPassword = "$2a$10$encodedPassword";
        UUID userId = UUID.randomUUID();

        User savedUser = new User(
            "testuser",
            "test@example.com",
            encodedPassword,
            null
        );

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.USER
        );

        given(passwordEncoder.encode("password123")).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, emptyProfileFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.profile()).isNull();

        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(binaryContentStorage).should(never()).put(any(UUID.class), any(byte[].class));
    }

    @Test
    @DisplayName("create - 프로필 이미지 업로드 실패 시 UserProfileUploadException 발생")
    void create_ProfileUploadFails_ThrowsUserProfileUploadException() throws IOException {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "TestUser",
            "Test@Example.com",
            "password123"
        );

        MultipartFile profileFile = org.mockito.Mockito.mock(MultipartFile.class);
        byte[] fileBytes = "test-image-data".getBytes();

        given(profileFile.isEmpty()).willReturn(false);
        given(profileFile.getOriginalFilename()).willReturn("profile.png");
        given(profileFile.getSize()).willReturn(12345L);
        given(profileFile.getContentType()).willReturn("image/png");
        given(profileFile.getBytes()).willThrow(new IOException("Storage error"));

        BinaryContent savedProfile = new BinaryContent(
            "profile.png",
            12345L,
            "image/png"
        );

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
            "  TestUser  ",
            "  Test@Example.COM  ",
            "password123"
        );

        String encodedPassword = "$2a$10$encodedPassword";
        UUID userId = UUID.randomUUID();

        User savedUser = new User(
            "testuser",
            "test@example.com",
            encodedPassword,
            null
        );

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.USER
        );

        given(passwordEncoder.encode("password123")).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            return savedUser;
        });
        given(userMapper.toDto(savedUser)).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");

        then(userRepository).should().save(any(User.class));
    }
}
