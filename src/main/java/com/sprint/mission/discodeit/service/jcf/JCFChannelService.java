package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        validate(channel);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Channel update(UUID id, Channel channelDto) {
        validate(channelDto);

        Channel channel = Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));

        channel.update(channelDto.getName(), channelDto.getDescription());
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(data.remove(id))
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
    }

    private void validate(Channel chDto) {
        if (chDto == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }
        if (chDto.getName() == null || chDto.getName().isBlank()) {
            throw new IllegalArgumentException("Channel name is required");
        }
        if (chDto.getDescription() == null) {
            throw new IllegalArgumentException("Channel description must not be null");
        }
    }
}
