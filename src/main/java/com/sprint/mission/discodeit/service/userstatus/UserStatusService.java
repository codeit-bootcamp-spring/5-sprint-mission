package com.sprint.mission.discodeit.service.userstatus;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatusService {

  private final UserStatusRepository userStatusRepository;

  protected UserStatus update(UUID id, Consumer<UserStatus> updater) {
    UserStatus entity = userStatusRepository.getOrThrow(id);
    updater.accept(entity);
    return userStatusRepository.save(entity);
  }

  @Transactional
  public UserStatusResponse create(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null.");
    userStatusRepository.findByUserId(userId).ifPresent(us -> {
      throw new IllegalArgumentException("User already has a status.");
    });
    return UserStatusResponse.from(userStatusRepository.save(new UserStatus(userId)));
  }


  public UserStatusResponse find(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return UserStatusResponse.from(userStatusRepository.getOrThrow(id));
  }

  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(UserStatusResponse::from)
        .toList();
  }

  public List<UserStatusResponse> findAllByUserId(Set<UUID> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return userStatusRepository.findAllByUserId(userIds).stream()
        .map(UserStatusResponse::from)
        .toList();
  }

  @Transactional
  public void heartbeat(UUID userId) {
    UserStatus us = userStatusRepository.findByUserId(userId)
        .orElseGet(() -> new UserStatus(userId));
    us.heartbeat();
    userStatusRepository.save(us);
  }

  @Transactional
  public void updateStatus(UUID userStatusId, UserStatusUpdateRequest req) {
    update(userStatusId, u -> u.setType(req.userStatusType()));
  }

  @Transactional
  public UserStatusResponse updateStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
    UserStatus us = userStatusRepository.getOrThrowByUserId(userId);
    return UserStatusResponse.from(update(us.getId(), u -> u.setType(req.userStatusType())));
  }

  @Transactional
  public boolean delete(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return userStatusRepository.softDeleteById(id);
  }
}
