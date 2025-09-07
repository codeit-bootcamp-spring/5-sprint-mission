package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));

    if (userStatusRepository.existsByUser_Id(userId)) {
      throw new IllegalArgumentException("UserStatus for userId " + userId + " already exists");
    }

    Instant lastActiveAt = request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now();
    return userStatusRepository.save(new UserStatus(user, lastActiveAt));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatus find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus us = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    us.update(request.newLastActiveAt());
    return us; // 변경감지로 반영
  }

  @Override
  public UserStatus updateByUserId(UUID userId, @Nullable UserStatusUpdateRequest request) {
    // 1) 유저 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));

    // 2) 없으면 생성(업서트), 있으면 가져오기
    UserStatus status = userStatusRepository.findByUser_Id(userId)
        .orElseGet(() -> userStatusRepository.save(new UserStatus(user, Instant.EPOCH)));

    // 3) 갱신 시간: 바디 없으면 now
    Instant candidate = (request != null && request.newLastActiveAt() != null)
        ? request.newLastActiveAt()
        : Instant.now();

    // 4) 단조 증가(과거로 되돌리지 않기)
    if (status.getLastActiveAt() == null || candidate.isAfter(status.getLastActiveAt())) {
      status.update(candidate); // 엔티티에 update(Instant) 존재
    }
    return status;
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}



