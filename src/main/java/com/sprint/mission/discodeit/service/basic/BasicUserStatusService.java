package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;                 // 🔹 추가
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException; // 🔹 추가
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;     // 🔹 추가
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();
    log.info("[USER_STATUS][CREATE] userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[USER_STATUS][CREATE] user not found userId={}", userId);
          return new UserNotFoundException(userId); // 🔹 교체
        });

    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          log.warn("[USER_STATUS][CREATE] already exists userId={}", userId);
          throw new UserStatusAlreadyExistsException(userId); // 🔹 교체
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);

    log.info("[USER_STATUS][CREATE][DONE] id={} userId={}", userStatus.getId(), userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("[USER_STATUS][FIND] id={}", userStatusId);
    return userStatusRepository.findById(userStatusId)
        .map(status -> {
          log.info("[USER_STATUS][FIND][DONE] id={}", userStatusId);
          return userStatusMapper.toDto(status);
        })
        .orElseThrow(() -> {
          log.warn("[USER_STATUS][FIND] not-found id={}", userStatusId);
          return new UserStatusNotFoundException(userStatusId); // 🔹 교체
        });
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("[USER_STATUS][FIND_ALL]");
    List<UserStatusDto> list = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
    log.info("[USER_STATUS][FIND_ALL][DONE] total={}", list.size());
    return list;
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();
    log.info("[USER_STATUS][UPDATE] id={} newLastActiveAt={}", userStatusId, newLastActiveAt);

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("[USER_STATUS][UPDATE] not-found id={}", userStatusId);
          return new UserStatusNotFoundException(userStatusId); // 🔹 교체
        });

    userStatus.update(newLastActiveAt);
    log.info("[USER_STATUS][UPDATE][DONE] id={}", userStatusId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();
    log.info("[USER_STATUS][UPDATE_BY_USER] userId={} newLastActiveAt={}", userId, newLastActiveAt);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("[USER_STATUS][UPDATE_BY_USER] not-found userId={}", userId);
          return new UserStatusNotFoundException(userId); // 🔹 교체
        });

    userStatus.update(newLastActiveAt);
    log.info("[USER_STATUS][UPDATE_BY_USER][DONE] userId={}", userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.info("[USER_STATUS][DELETE] id={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("[USER_STATUS][DELETE] not-found id={}", userStatusId);
      throw new UserStatusNotFoundException(userStatusId); // 🔹 교체
    }
    userStatusRepository.deleteById(userStatusId);
    log.info("[USER_STATUS][DELETE][DONE] id={}", userStatusId);
  }
}
