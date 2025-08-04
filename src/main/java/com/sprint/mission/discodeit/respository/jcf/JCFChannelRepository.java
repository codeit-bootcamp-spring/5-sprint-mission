package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(ChannelDto.Create dto) {
        Channel channel = new Channel(dto.name(), dto.type());
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findByName(String name) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : data.values()) {
            if (channel.getName().equals(name)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
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
    public Optional<Channel> updateTopic(UUID id, String topic) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateTopic(topic);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
