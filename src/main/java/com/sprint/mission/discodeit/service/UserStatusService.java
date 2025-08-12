package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;

    private static UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(userStatus.getUserId(), userStatus.getType());
    }

    protected void update(UUID id, Consumer<UserStatus> updater) {
        UserStatus entity = userStatusRepository.getOrThrow(id);
        updater.accept(entity);
        userStatusRepository.save(entity);
    }


    public UserStatusResponse create(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        userStatusRepository.findByUserId(userId).ifPresent(us -> {
            throw new IllegalArgumentException("User already has a status.");
        });
        return toResponse(userStatusRepository.save(new UserStatus(userId)));
    }


    public UserStatusResponse find(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return toResponse(userStatusRepository.getOrThrow(id));
    }

    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusService::toResponse)
                .toList();
    }

    public List<UserStatusResponse> findAllByUserIds(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return userStatusRepository.findAllByUserIds(userIds).stream()
                .map(UserStatusService::toResponse)
                .toList();
    }

    public void updateStatus(UUID userStatusId, UserStatusUpdateRequest req) {
        Objects.requireNonNull(req, "req must not be null");
        Objects.requireNonNull(userStatusId, "userStatusId must not be null");

        update(userStatusId, u -> u.setType(req.userStatusType()));
    }

    public void updateByUserId(UUID userId, UserStatusUpdateRequest req) {
        Objects.requireNonNull(req, "req must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        UserStatus us = userStatusRepository.getOrThrowByUserId(userId);
        update(us.getId(), u -> u.setType(req.userStatusType()));
    }

    public boolean delete(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return userStatusRepository.softDeleteById(id);
    }
}
