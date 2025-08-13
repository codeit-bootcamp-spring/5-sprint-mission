package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ChannelParticipant;
import com.sprint.mission.discodeit.repository.ChannelParticipantRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFChannelParticipantRepository implements ChannelParticipantRepository {
    private final Map<UUID, ChannelParticipant> data;

    public JCFChannelParticipantRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ChannelParticipant save(ChannelParticipant channelParticipant) {
        data.put(channelParticipant.getId(), channelParticipant);
        return channelParticipant;
    }

    @Override
    public Optional<ChannelParticipant> findByUserId(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ChannelParticipant> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(cp -> cp.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelParticipant> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(cp -> cp.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        return data.values().stream()
                .anyMatch(cp -> cp.getChannelId().equals(channelId) && cp.getUserId().equals(userId));
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        data.values().removeIf(cp -> cp.getChannelId().equals(channelId) && cp.getUserId().equals(userId));
    }
}
