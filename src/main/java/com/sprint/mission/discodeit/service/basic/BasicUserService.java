package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
      throw new IllegalArgumentException("이미 사용 중인 username입니다.");
    }

    if (userRepository.existsByEmail(create.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 email입니다.");
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
    userStatusRepository.save(status);

    return userMapper.toDetail(user);
  }

  @Override
  public UserDto.Detail findById(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new RuntimeException("User not found"));

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
                              .orElseThrow(() -> new RuntimeException("User not found"));

    BinaryContent oldProfile = user.getProfile();
    BinaryContent newProfile = null;
    if (update.getProfileImage() != null && !update.getProfileImage()
                                                   .isEmpty()) {

      newProfile = binaryContentService.create(
          new BinaryContentDto.CreateCommand(update.getProfileImage()));
    }

    user.update(update, newProfile);
    userRepository.save(user);

    if (oldProfile != null) {
      binaryContentService.delete(oldProfile.getId());
    }

    return userMapper.toDetail(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new RuntimeException("User not found"));

    userStatusRepository.delete(user.getStatus());
    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile()
                                      .getId());
    }

    userRepository.delete(user);
  }

  @Override
  @Transactional
  public void deleteAll() {
    userRepository.deleteAll();
  }
}
