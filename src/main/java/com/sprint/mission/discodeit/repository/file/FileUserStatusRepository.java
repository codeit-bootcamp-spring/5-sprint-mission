package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public UserStatus save(UserStatus status) {
        storage.put(status.getUserId(), status);
        return status;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        storage.remove(userId);
    }
}
