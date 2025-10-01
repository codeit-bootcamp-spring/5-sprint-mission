package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;

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

    User user = userMapper.toEntity(create, profile);
    userRepository.save(user);

    UserStatus status = UserStatus.builder()
                                  .user(user)
                                  .lastActiveAt(Instant.now())
                                  .build();

    user.updateStatus(status);
    userStatusRepository.save(status);

    log.info("User {} created", user.getUsername());

    return userMapper.toDetail(user);
  }

  @Override
  public UserDto.Detail findById(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new UserNotFoundException(userId));

    return userMapper.toDetail(user);
  }

  @Override
  public List<UserDto.Detail> findAll() {

    List<User> users = userRepository.findAll();

    return users.stream()
                .map(userMapper::toDetail)
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

    user.update(update, newProfile);

    if (oldProfile != null) {
      binaryContentService.delete(oldProfile.getId());
    }

    log.info("User {} updated", user.getUsername());

    return userMapper.toDetail(user);
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
