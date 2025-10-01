package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    log.info("유저 상태 생성 요청: userId={}", userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> {
              log.error("유저 상태 생성 실패 - 유저 없음: userId={}", userId);
              return new UserNotFoundException();
            });

    Optional.ofNullable(user.getStatus())
            .ifPresent(status -> {
              log.warn("유저 상태 생성 실패 - 이미 존재: userId={}", userId);
              throw new UserStatusAlreadyExistsException();
            });

    UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
    userStatusRepository.save(userStatus);
    log.info("유저 상태 생성 성공: userStatusId={}", userStatus.getId());

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.info("유저 상태 조회 요청: userStatusId={}", userStatusId);
    return userStatusRepository.findById(userStatusId)
            .map(userStatusMapper::toDto)
            .orElseThrow(() -> {
              log.error("유저 상태 조회 실패 - 없음: userStatusId={}", userStatusId);
              return new UserStatusNotFoundException();
            });
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.info("전체 유저 상태 조회 요청");
    List<UserStatusDto> statuses = userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
    log.info("전체 유저 상태 조회 완료: {}건", statuses.size());
    return statuses;
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.info("유저 상태 수정 요청: userStatusId={}", userStatusId);

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> {
              log.error("유저 상태 수정 실패 - 없음: userStatusId={}", userStatusId);
              return new UserStatusNotFoundException();
            });

    userStatus.update(request.newLastActiveAt());
    log.info("유저 상태 수정 성공: userStatusId={}", userStatusId);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.info("유저 상태 수정 요청 (userId 기반): userId={}", userId);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> {
              log.error("유저 상태 수정 실패 - 해당 userId 상태 없음: userId={}", userId);
              return new UserStatusNotFoundException();
            });

    userStatus.update(request.newLastActiveAt());
    log.info("유저 상태 수정 성공: userId={}", userId);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.info("유저 상태 삭제 요청: userStatusId={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      log.error("유저 상태 삭제 실패 - 없음: userStatusId={}", userStatusId);
      throw new UserStatusNotFoundException();
    }

    userStatusRepository.deleteById(userStatusId);
    log.info("유저 상태 삭제 성공: userStatusId={}", userStatusId);
  }
}