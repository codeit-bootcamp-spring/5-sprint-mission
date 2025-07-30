package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        if (isNotValid(channel)) {
            throw new IllegalArgumentException("invalid channel data");
        }
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("channel not found");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Channel update(UUID id, Channel channelDto) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("channel not found");
        }

        if (isNotValid(channelDto)) {
            throw new IllegalArgumentException("invalid channel data");
        }

        channel.update(channelDto.getName(), channelDto.getDescription());

        return channel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    private boolean isNotValid(Channel ch) {
        return ch == null || ch.getName() == null || ch.getName().isBlank()
                || ch.getDescription() == null;
    }

}
