package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.Create;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserStatusDto.Detail create(Create request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("Not Found User"));

    UserStatus userStatus = userStatusRepository.findByUserId(request.getUserId()).orElse(null);
    if (userStatus != null) {
      throw new IllegalArgumentException("Already Registered User Status");
    }

    userStatus = userStatusRepository.save(UserStatus.of(request.getUserId()));

    return UserStatusDto.Detail.builder().id(userStatus.getId())
        .userId(userStatus.getUserId()).lastLogin(userStatus.getLastLogin()).build();
  }

  @Override
  public UserStatusDto.Detail find(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Not Found User Status"));

    return UserStatusDto.Detail.builder().id(userStatus.getId())
        .userId(userStatus.getUserId()).lastLogin(userStatus.getLastLogin()).build();
  }

  @Override
  public List<UserStatusDto.Detail> findAll() {
    List<UserStatus> userStatuses = userStatusRepository.findAll();

    return userStatuses.stream().map(
        us -> UserStatusDto.Detail.builder().id(us.getId()).userId(us.getUserId())
            .lastLogin(us.getLastLogin()).build()).toList();
  }

  @Override
  public UserStatusDto.Detail update(UUID id) {

    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Not Found User Status"));

    userStatus.update();

    userStatusRepository.save(userStatus);

    return UserStatusDto.Detail.builder().id(userStatus.getId())
        .userId(userStatus.getUserId()).lastLogin(userStatus.getLastLogin()).build();
  }

  @Override
  public UserStatusDto.Detail updateByUserId(UUID userId) {

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Not Found User Status"));

    userStatus.update();
    userStatusRepository.save(userStatus);

    return UserStatusDto.Detail.builder().id(userStatus.getId())
        .userId(userStatus.getUserId()).lastLogin(userStatus.getLastLogin()).build();
  }

  @Override
  public void delete(UUID id) {
    userStatusRepository.delete(id);
  }

  @Override
  public void deleteAll() {
    userStatusRepository.deleteAll();
  }
}
