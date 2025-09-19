package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
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

    UserStatus userStatus = userStatusRepository.findByUserId(request.userId()).orElse(null);

    if (userStatus != null) {
      throw UserStatusAlreadyExistsException.withDetail("id", userStatus.getId());
    }

    userStatus = new UserStatus(user, Instant.now());
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findById(UUID id) {
    return userStatusMapper.toDto(validateId(id));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findByUserId(UUID userid) {
    return userStatusMapper.toDto(validateId(userid));
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
    UserStatus userStatus = validateId(userStatusId);
    userStatus.update(userStatusUpdateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId,
      @Valid UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus userStatus = validateUserId(userId);

    userStatus.update(userStatusUpdateRequest.newLastActiveAt());

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    UserStatus userStatus = validateId(id);
    userStatusRepository.deleteById(userStatus.getId());
  }

  private UserStatus validateId(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> UserStatusNotFoundException.withDetail("id", id));
  }

  private UserStatus validateUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> UserStatusNotFoundException.withDetail("userId", userId));
  }
}
