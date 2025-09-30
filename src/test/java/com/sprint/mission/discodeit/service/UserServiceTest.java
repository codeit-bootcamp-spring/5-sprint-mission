package com.sprint.mission.discodeit.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentStorage  binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;

  @InjectMocks
  private BasicUserService userService;

  private String username;
  private String email;
  private String password;
  private UUID userId;

  private UserDto userDto;
  private BinaryContent  binaryContent;
  private User user;


  @BeforeEach
  void setUp() {
    username="test01";
    email="test01@email.com";
    password="test01";
    userId=UUID.randomUUID();

    user=User.builder()
        .username(username)
        .email(email)
        .password(password)
        .profile(null)
        .build();

    userDto =new UserDto(
        userId,
        username,
        email,
        null,
        false
    );
  }

  @Test
  void createUserWithoutProfile() {
    // Given
    UserCreateRequest req = new UserCreateRequest(username, email, password);

    // 1. Stub the checks (user does not exist)
    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(false);

    // 2. Stub the saving process
    given(userRepository.save(any(User.class))).willReturn(user);

    // 3. Stub the mapper (Crucial: The service returns the DTO)
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // When
    // Pass Optional.empty() for the profile request
    UserDto result = userService.create(req, Optional.empty());

    // Then
    // 1. Assertions on the result
    assertNotNull(result);
    assertEquals(username, result.username());
    assertEquals(email, result.email());

    // 2. Verification of mock calls (ensuring the correct methods were called)
    verify(userRepository, times(1)).existsByEmail(email);
    verify(userRepository, times(1)).existsByUsername(username);
    verify(userRepository, times(1)).save(any(User.class));
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(binaryContentStorage, never()).put(any(), any());
    verify(userMapper, times(1)).toDto(any(User.class));
  }

  @Test
  @DisplayName("프로필 이미지를 변경하지 않고 사용자 정보를 수정한다.")
  void update_withoutProfileChange_success() {
    // Given
    UUID existingUserId = userId;
    String newUsername = "newTest01";
    String newEmail = "newtest01@email.com";
    String newPassword = "newPassword";

    UserUpdateRequest updateReq = new UserUpdateRequest(newUsername, newEmail, newPassword);

    // 1. Stubbing: 사용자 존재 확인 및 중복 확인
    given(userRepository.findById(existingUserId)).willReturn(Optional.of(user)); // 기존 사용자 찾기
    given(userRepository.existsByEmail(newEmail)).willReturn(false); // 이메일 중복 없음
    given(userRepository.existsByUsername(newUsername)).willReturn(false); // 사용자 이름 중복 없음

    // 2. Stubbing: 매퍼 (수정된 결과를 DTO로 변환)
    UserDto updatedDto = new UserDto(existingUserId, newUsername, newEmail, null, false);
    given(userMapper.toDto(any(User.class))).willReturn(updatedDto);

    // When
    UserDto result = userService.update(existingUserId, updateReq, Optional.empty());

    // Then
    // 1. 결과 검증
    assertNotNull(result);
    assertEquals(newUsername, result.username());
    assertEquals(newEmail, result.email());

    // 2. Mock 호출 검증
    verify(userRepository, times(1)).findById(existingUserId);
    verify(userRepository, times(1)).existsByEmail(newEmail);
    verify(userRepository, times(1)).existsByUsername(newUsername);
    // 프로필 변경이 없으므로 파일 관련 메서드는 호출되지 않아야 함
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(binaryContentStorage, never()).put(any(), any());
    // user.update()가 내부적으로 호출되었고, 최종적으로 save 없이 toDto로 넘어간다고 가정합니다.
    verify(userMapper, times(1)).toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 ID로 사용자를 성공적으로 삭제한다.")
  void delete_success() {
    // Given
    UUID existingUserId = userId;
    given(userRepository.existsById(existingUserId)).willReturn(true);

    // When
    userService.delete(existingUserId);

    // Then
    // existsById가 호출되고 deleteById가 호출되었는지 검증
    verify(userRepository, times(1)).existsById(existingUserId);
    verify(userRepository, times(1)).deleteById(existingUserId);
  }



}
