package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> storage = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        storage.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return storage.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return storage.values().stream()
                .filter(r -> r.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.values().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.values().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.getChannelId().equals(channelId));
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        storage.values().removeIf(r -> r.getUserId().equals(userId));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        storage.values().removeIf(r -> r.getChannelId().equals(channelId));
    }
}
