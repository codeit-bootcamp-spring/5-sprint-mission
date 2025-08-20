package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findByName(String name) {
        return data.values().stream()
                .filter(c -> c.getName() != null)
                .filter(c -> c.getName().equals(name))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public List<Channel> findAllById(List<UUID> ids) {
        return data.values().stream()
                .filter(c -> ids.contains(c.getId()))
                .toList();
    }

    @Override
    public Optional<Channel> updateName(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateName(name);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
