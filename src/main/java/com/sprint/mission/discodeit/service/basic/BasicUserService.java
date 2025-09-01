package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("userService")
@RequiredArgsConstructor
@Validated
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto create(@Valid UserCommand userCommand) {
    String username = userCommand.username();
    String password = userCommand.password();
    String email = userCommand.email();

    BinaryContent profile = userCommand.profile().stream()
        .map(dto -> {
          BinaryContent binaryContent = new BinaryContent(
              dto.fileName(),
              dto.contentType(),
              dto.bytes().length);
          binaryContentStorage.put(binaryContent.getId(), dto.bytes());
          return binaryContentRepository.save(binaryContent);
        })
        .findFirst()
        .orElse(null);

    User user = new User(username, email, password, profile);
    UserStatus userStatus = new UserStatus();
    userStatus.setLastActiveAt(Instant.now());
    user.attachStatus(userStatus);

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID userId) {
    return userMapper.toDto(
        userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("findById : 유저를 찾을 수 없습니다. [" + userId + "]")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, @Valid UserCommand userCommand) {
    String newUserName = userCommand.username();
    String newPassword = userCommand.password();
    String newEmail = userCommand.email();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("update : 유저를 찾을 수 없습니다. [" + userId + "]"));

    BinaryContent newProfile = userCommand.profile().stream()
        .map(dto -> {
          BinaryContent binaryContent = new BinaryContent(
              dto.fileName(),
              dto.contentType(),
              dto.bytes().length);
          binaryContentStorage.put(binaryContent.getId(), dto.bytes());
          return binaryContentRepository.save(binaryContent);
        })
        .findFirst()
        .orElse(null);

    if (newProfile != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    user.update(newUserName, newEmail, newPassword, newProfile);

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("delete : 유저를 찾을 수 없습니다. [" + userId + "]"));

    if (user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    userStatusRepository.findByUserId(user.getId())
        .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

    userRepository.deleteById(user.getId());
  }
}
