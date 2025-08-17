package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class JCFUserStatusRepository implements UserStatusRepository {

    private static final ConcurrentHashMap<UUID, UserStatus> storage = new ConcurrentHashMap<>();

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
        return storage.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus us : storage.values()) {
            if (us.getUserId().equals(userId)) {
                return Optional.of(us);
            }
        }
        return Optional.empty();
    }
}