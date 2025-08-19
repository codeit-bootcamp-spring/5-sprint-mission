package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> userStatusMap = new HashMap<>();

    @Override
    public boolean existsByUserId(UUID userId) {
        return userStatusMap.values().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(userId));
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        return userStatusMap.put(userStatus.getUserId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return userStatusMap.values().stream()
                .filter(status -> status.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusMap.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusMap.values().stream().toList();
    }

    @Override
    public boolean delete(UUID id) {
        return userStatusMap.remove(id) != null;
    }
}
