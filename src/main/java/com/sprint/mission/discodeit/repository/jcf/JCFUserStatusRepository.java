package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        storage.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return storage.values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean deleteByUserId(UUID userId) {
        storage.values().removeIf(us -> us.getUserId().equals(userId));
        return false;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return storage.values().stream()
                .anyMatch(status -> status.getUserId().equals(userId));
    }
}
