package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final SessionManager sessionManager;

  @Override
  @Transactional
  public UserDto.Detail create(CreateCommand create) {

    if (userRepository.existsByUsername(create.getUsername())) {
      throw new UserDuplicateException(create.getUsername());
    }

    if (userRepository.existsByEmail(create.getEmail())) {
      throw new UserDuplicateException(create.getEmail());
    }

    BinaryContent profile = null;
    if (create.getProfileImage() != null && !create.getProfileImage()
                                                   .isEmpty()) {

      profile = binaryContentService.create(
          new BinaryContentDto.CreateCommand(create.getProfileImage()));
    }

    String encodedPassword = passwordEncoder.encode(create.getPassword());

    User user = userMapper.toEntity(create, profile, encodedPassword);
    userRepository.save(user);

    log.info("User {} created", user.getUsername());

    return userMapper.toDetail(user, sessionManager.isUserOnline(user.getId()));
  }

  @Override
  public UserDto.Detail findById(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new UserNotFoundException(userId));

    return userMapper.toDetail(user, sessionManager.isUserOnline(user.getId()));
  }

  @Override
  public List<UserDto.Detail> findAll() {

    List<User> users = userRepository.findAll();

    return users.stream()
                .map(user -> userMapper.toDetail(user, sessionManager.isUserOnline(user.getId())))
                .toList();
  }

  @Override
  @Transactional
  public UserDto.Detail update(UpdateCommand update) {

    User user = userRepository.findById(update.getId())
                              .orElseThrow(() -> new UserNotFoundException(update.getId()));

    BinaryContent oldProfile = user.getProfile();
    BinaryContent newProfile = null;
    if (update.getProfileImage() != null && !update.getProfileImage()
                                                   .isEmpty()) {

      newProfile = binaryContentService.create(
          new BinaryContentDto.CreateCommand(update.getProfileImage()));
    }

    String encodedPassword =
        update.getPassword() != null ? passwordEncoder.encode(update.getPassword()) : null;

    UpdateCommand updateWithEncodedPassword = UpdateCommand.builder()
                                                           .id(update.getId())
                                                           .username(update.getUsername())
                                                           .email(update.getEmail())
                                                           .password(encodedPassword)
                                                           .profileImage(update.getProfileImage())
                                                           .role(update.getRole())
                                                           .build();

    if (!user.getRole()
             .equals(update.getRole())) {

      sessionManager.expireSessionsForUser(user.getId());
    }

    user.update(updateWithEncodedPassword, newProfile);

    if (oldProfile != null) {
      binaryContentService.delete(oldProfile.getId());
    }

    log.info("User {} updated", user.getUsername());

    return userMapper.toDetail(user, sessionManager.isUserOnline(user.getId()));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new UserNotFoundException(userId));

    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile()
                                      .getId());
    }

    userRepository.delete(user);

    log.info("User {} deleted", user.getUsername());
  }

  @Override
  @Transactional
  public void deleteAll() {
    userRepository.deleteAll();
  }
}
