package com.sprint.mission.discodeit.service.userstatus;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.status.UserStatusHeartbeatRequest;
import com.sprint.mission.discodeit.dto.request.status.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.sprint.mission.discodeit.mapper.UserStatusMapper.toUserStatusResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    protected void update(UUID id, Consumer<UserStatus> updater) {
        UserStatus entity = userStatusRepository.getOrThrow(id);
        updater.accept(entity);
        userStatusRepository.save(entity);
    }

    @Transactional
    public UserStatusResponse create(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        userStatusRepository.findByUserId(userId).ifPresent(us -> {
            throw new IllegalArgumentException("User already has a status.");
        });
        return toUserStatusResponse(userStatusRepository.save(new UserStatus(userId)));
    }


    public UserStatusResponse find(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return toUserStatusResponse(userStatusRepository.getOrThrow(id));
    }

    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusMapper::toUserStatusResponse)
                .toList();
    }

    public List<UserStatusResponse> findAllByUserIds(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return userStatusRepository.findAllByUserIds(userIds).stream()
                .map(UserStatusMapper::toUserStatusResponse)
                .toList();
    }

    @Transactional
    public void heartbeat(UserStatusHeartbeatRequest req) {
        UserStatus us = userStatusRepository.findByUserId(req.userId()).orElseGet(() -> new UserStatus(req.userId()));
        us.heartbeat();
        userStatusRepository.save(us);
    }

    @Transactional
    public void updateStatus(UUID userStatusId, UserStatusUpdateRequest req) {
        update(userStatusId, u -> u.setType(req.userStatusType()));
    }

    @Transactional
    public void updateStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
        UserStatus us = userStatusRepository.findByUserId(userId).orElseGet(() -> new UserStatus(userId));
        update(us.getId(), u -> u.setType(req.userStatusType()));
    }

    @Transactional
    public boolean delete(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return userStatusRepository.softDeleteById(id);
    }
}
