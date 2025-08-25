package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
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
      profile = BinaryContent.of(create.getProfileImage());
      binaryContentRepository.save(profile);
    }

    User user = User.of(create, profile != null ? profile.getId() : null);
    userRepository.save(user);

    UserStatus status = UserStatus.of(user.getId());
    userStatusRepository.save(status);

    return UserDto.Detail.builder()
                         .id(user.getId())
                         .username(user.getName())
                         .email(user.getEmail())
                         .profileId(user.getProfileId())
                         .online(status.isOnline())
                         .createdAt(user.getCreatedAt())
                         .updatedAt(user.getUpdatedAt())
                         .build();
  }

  @Override
  public UserDto.Detail findById(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new RuntimeException("User not found"));

    UserStatus status = userStatusRepository.findByUserId(userId)
                                            .orElse(null);

    return UserDto.Detail.builder()
                         .id(user.getId())
                         .username(user.getName())
                         .email(user.getEmail())
                         .profileId(user.getProfileId())
                         .online(status != null && status.isOnline())
                         .createdAt(user.getCreatedAt())
                         .updatedAt(user.getUpdatedAt())
                         .build();
  }

  @Override
  public List<UserDto.Detail> findAll() {

    List<User> users = userRepository.findAll();
    List<UserStatus> status = userStatusRepository.findAll();

    return users.stream()
                .map(u -> {
                  UserStatus s = status.stream()
                                       .filter(t -> t.getUserId()
                                                     .equals(u.getId()))
                                       .findFirst()
                                       .orElse(null);

                  return UserDto.Detail.builder()
                                       .id(u.getId())
                                       .username(u.getName())
                                       .email(u.getEmail())
                                       .profileId(u.getProfileId())
                                       .online(s != null && s.isOnline())
                                       .createdAt(u.getCreatedAt())
                                       .updatedAt(u.getUpdatedAt())
                                       .build();
                })
                .toList();
  }

  @Override
  public UserDto.Detail update(UpdateCommand update) {

    User user = userRepository.findById(update.getId())
                              .orElseThrow(() -> new RuntimeException("User not found"));

    UUID newProfileId = user.getProfileId();
    if (update.getProfileImage() != null && !update.getProfileImage()
                                                   .isEmpty()) {
      BinaryContent newProfile = BinaryContent.of(update.getProfileImage());
      binaryContentRepository.save(newProfile);
      newProfileId = newProfile.getId();
    }

    user.update(update, newProfileId);
    userRepository.save(user);

    UserStatus status = userStatusRepository.findByUserId(update.getId())
                                            .orElse(null);

    return UserDto.Detail.builder()
                         .id(user.getId())
                         .username(user.getName())
                         .email(user.getEmail())
                         .profileId(user.getProfileId())
                         .online(status != null && status.isOnline())
                         .createdAt(user.getCreatedAt())
                         .updatedAt(user.getUpdatedAt())
                         .build();
  }

  @Override
  public void delete(UUID userId) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new RuntimeException("User not found"));

    userStatusRepository.delete(userId);
    if (user.getProfileId() != null) {
      binaryContentRepository.delete(user.getProfileId());
    }

    userRepository.delete(userId);
  }

  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }
}
