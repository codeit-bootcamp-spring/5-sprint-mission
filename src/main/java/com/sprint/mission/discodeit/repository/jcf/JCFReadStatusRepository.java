package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository("readStatusRepository")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() { this.data = new HashMap<>(); }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return this.data.values().stream().filter(rs -> rs.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.data.values().stream().filter(rs -> rs.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        this.data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
    }


}
