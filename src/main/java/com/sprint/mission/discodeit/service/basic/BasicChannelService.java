package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(Channel channel) {

        if (channel == null) {
            return null;
        }

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID adminUserId) {

        if (type == null || name == null || adminUserId == null) {
            return null;
        }

        return channelRepository.save(new Channel(type, name, description, adminUserId));
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel get(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            return null;
        }

        channel.update(name, description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel != null) {
            channelRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
    }
}
