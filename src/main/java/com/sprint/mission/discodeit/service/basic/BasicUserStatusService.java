package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.Create;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto.Detail create(Create request) {
    User user = userRepository.findById(request.getUserId())
                              .orElseThrow(() -> new RuntimeException("Not Found User"));

    UserStatus userStatus = userStatusRepository.findByUserId(request.getUserId())
                                                .orElse(null);
    if (userStatus != null) {
      throw new IllegalArgumentException("Already Registered User Status");
    }

    userStatus = userStatusRepository.save(UserStatus.builder()
                                                     .user(user)
                                                     .lastActiveAt(Instant.now())
                                                     .build());

    return userStatusMapper.toDetail(userStatus);
  }

  @Override
  public UserStatusDto.Detail find(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
                                                .orElseThrow(() -> new RuntimeException(
                                                    "Not Found User Status"));

    return userStatusMapper.toDetail(userStatus);
  }

  @Override
  public List<UserStatusDto.Detail> findAll() {
    List<UserStatus> userStatuses = userStatusRepository.findAll();

    return userStatuses.stream()
                       .map(userStatusMapper::toDetail)
                       .toList();
  }

  @Override
  @Transactional
  public UserStatusDto.Detail update(UUID id) {

    UserStatus userStatus = userStatusRepository.findById(id)
                                                .orElseThrow(() -> new RuntimeException(
                                                    "Not Found User Status"));

    userStatus.update();

    userStatusRepository.save(userStatus);

    return userStatusMapper.toDetail(userStatus);
  }

  @Override
  @Transactional
  public UserStatusDto.Detail updateByUserId(UUID userId) {

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
                                                .orElseThrow(() -> new RuntimeException(
                                                    "Not Found User Status"));

    userStatus.update();
    userStatusRepository.save(userStatus);

    return userStatusMapper.toDetail(userStatus);
  }

  @Override
  @Transactional
  public void delete(UUID id) {

    UserStatus userStatus = userStatusRepository.findById(id)
                                                .orElseThrow(() -> new RuntimeException(
                                                    "Not Found User Status"));

    userStatusRepository.delete(userStatus);
  }

  @Override
  @Transactional
  public void deleteAll() {
    userStatusRepository.deleteAll();
  }
}
