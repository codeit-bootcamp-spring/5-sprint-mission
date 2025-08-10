package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus entity) {
        this.data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : this.data.values()) {
            if (Objects.equals(rs.getUserId(), userId) &&
                    Objects.equals(rs.getChannelId(), channelId)) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : this.data.values()) {
            if (Objects.equals(rs.getUserId(), userId)) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }
}
