package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("test")
public class JCFChannelRepository implements ChannelRepository {
    private static final Map<UUID, Channel> data = new ConcurrentHashMap<>();

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
    public List<Channel> findAll() {
        return data.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public Optional<Channel> findByChannelName(String channelName) {
        return data.values().stream()
                .filter(channel -> channel.getChannelName() != null && channel.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
