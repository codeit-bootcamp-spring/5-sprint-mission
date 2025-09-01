package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("userStatusService")
@RequiredArgsConstructor
@Validated
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(@Valid UserStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException(
            "create : 유저를 찾을 수 없습니다. [" + request.userId() + "]"));

    UserStatus userStatus = userStatusRepository.findAll().stream()
        .filter(s -> s.getUser().getId().equals(request.userId()))
        .findFirst()
        .orElse(null);

    if (userStatus != null) {
      throw new IllegalArgumentException(
          "create : UserStatus가 이미 존재합니다. [" + request.userId() + "]");
    }

    userStatus = new UserStatus(user, Instant.now());
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findById(UUID id) {
    return userStatusMapper.toDto(
        userStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "findById : UserStatus를 찾을 수 없습니다. [" + id + "]")));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findByUserId(UUID userid) {
    return userStatusMapper.toDto(
        userStatusRepository.findByUserId(userid)
            .orElseThrow(() -> new NoSuchElementException(
                "findByUserId : UserStatus를 찾을 수 없습니다. [" + userid + "]")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId,
      @Valid UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            "update : UserStatus를 찾을 수 없습니다. [" + userStatusId + "]"));
    userStatus.update(userStatusUpdateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId,
      @Valid UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException(
            "updateByUserId : UserStatus를 찾을 수 없습니다. [" + userId + "]"));
    userStatus.update(userStatusUpdateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("delete : UserStatus를 찾을 수 없습니다. [" + id + "]"));
    userStatusRepository.deleteById(userStatus.getId());
  }
}
