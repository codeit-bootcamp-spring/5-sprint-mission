package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    Map<UUID, ReadStatus> readStatusMap;

    public JCFReadStatusRepository() {
        readStatusMap = new HashMap<>();
    }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(readStatusMap.get(id));
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        List<ReadStatus> readStatuses = new ArrayList<>();
        readStatusMap.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .forEach(readStatuses::add);
        return readStatuses;
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = new ArrayList<>();
        readStatusMap.values().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .forEach(readStatuses::add);
        return readStatuses;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusMap.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return readStatusMap.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        readStatusMap.remove(id);
    }
}
