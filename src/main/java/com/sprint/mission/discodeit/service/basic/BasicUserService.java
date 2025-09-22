package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto create(UserCommand userCommand) {
    String username = validateUsername(userCommand.username());
    String password = userCommand.password();
    String email = validateEmail(userCommand.email());
    BinaryContent profile = profileMapper(userCommand.profile());

    User user = new User(username, email, password, profile);
    UserStatus userStatus = new UserStatus();
    userStatus.setLastActiveAt(Instant.now());
    user.attachStatus(userStatus);

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID userId) {
    return userMapper.toDto(validateId(userId));
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
  public UserDto update(UUID userId, UserCommand userCommand) {
    String newUserName = validateUsername(userCommand.username());
    String newPassword = userCommand.password();
    String newEmail = validateEmail(userCommand.email());

    User user = validateId(userId);

    BinaryContent newProfile = profileMapper(userCommand.profile());

    if (newProfile != null && user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    user.update(newUserName, newEmail, newPassword, newProfile);

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = validateId(userId);

    if (user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    userStatusRepository.findByUserId(user.getId())
        .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

    userRepository.deleteById(user.getId());
  }

  private BinaryContent profileMapper(@Valid Optional<NewBinaryContent> profile) {
    return profile.stream()
        .map(dto -> {
          BinaryContent binaryContent = new BinaryContent(
              dto.fileName(),
              dto.contentType(),
              dto.bytes().length);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), dto.bytes());
          return binaryContent;
        })
        .findFirst()
        .orElse(null);
  }

  private User validateId(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withDetail("userId", userId));
  }

  private String validateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withDetail("username", username);
    }
    return username;
  }

  private String validateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withDetail("email", email);
    }
    return email;
  }
}
