package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.Detail;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SessionManager sessionManager;

  @InjectMocks
  private BasicUserService userService;

  private UUID userId;
  private CreateCommand createCommand;
  private UpdateCommand updateCommand;
  private User user;
  private Detail detail;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    BinaryContent profile = BinaryContent.builder()
                                         .build();
    user = User.builder()
               .username("testuser")
               .email("test@example.com")
               .password("password")
               .profile(profile)
               .role(UserRole.USER.name())
               .build();

    createCommand = CreateCommand.builder()
                                 .username("testuser")
                                 .email("test@example.com")
                                 .password("password")
                                 .profileImage(mock(MultipartFile.class))
                                 .build();

    updateCommand = UpdateCommand.builder()
                                 .id(userId)
                                 .username("updated")
                                 .email("updated@example.com")
                                 .password("newpass")
                                 .profileImage(mock(MultipartFile.class))
                                 .build();

    detail = Detail.builder()
                   .id(userId)
                   .username(user.getUsername())
                   .email(user.getEmail())
                   .profile(null)
                   .online(false)
                   .build();
  }

  @Test
  void createUser_success() {
    given(userRepository.existsByUsername(createCommand.getUsername())).willReturn(false);
    given(userRepository.existsByEmail(createCommand.getEmail())).willReturn(false);
    doReturn(user).when(userMapper)
                  .toEntity(any(CreateCommand.class), any(), any());
    doReturn(detail).when(userMapper)
                    .toDetail(any(User.class), eq(false));

    Detail result = userService.create(createCommand);

    then(userRepository).should()
                        .save(user);
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(user.getUsername());
  }

  @Test
  void createUser_duplicateUsername_fail() {
    given(userRepository.existsByUsername(createCommand.getUsername())).willReturn(true);

    assertThrows(UserDuplicateException.class, () -> userService.create(createCommand));
  }

  @Test
  void createUser_duplicateEmail_fail() {
    given(userRepository.existsByUsername(createCommand.getUsername())).willReturn(false);
    given(userRepository.existsByEmail(createCommand.getEmail())).willReturn(true);

    assertThrows(UserDuplicateException.class, () -> userService.create(createCommand));
  }

  @Test
  void updateUser_success() {
    // given
    given(userRepository.findById(updateCommand.getId())).willReturn(Optional.of(user));
    doReturn(detail).when(userMapper)
                    .toDetail(any(User.class), eq(false));
    given(binaryContentService.create(any())).willReturn(mock(BinaryContent.class));

    // when
    Detail result = userService.update(updateCommand);

    // then
    assertThat(user.getUsername()).isEqualTo(updateCommand.getUsername());
    assertThat(user.getEmail()).isEqualTo(updateCommand.getEmail());
    assertThat(result).isNotNull();
  }


  @Test
  void updateUser_notFound_fail() {
    given(userRepository.findById(updateCommand.getId())).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(updateCommand));
  }

  @Test
  void deleteUser_success() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.delete(userId);

    then(userRepository).should()
                        .delete(user);
  }

  @Test
  void deleteUser_notFound_fail() {
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }
}
