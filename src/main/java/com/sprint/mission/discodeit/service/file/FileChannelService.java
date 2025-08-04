package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import com.sprint.mission.discodeit.respository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository = new FileChannelRepository();


    @Override
    public Channel create(String name, ChannelType type) {
        Channel channel = new Channel(name, type);
        return channelRepository.save(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelRepository.findById(id));
    }

    @Override
    public List<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        if (channel != null) {
            channel.updateName(name);
            channelRepository.save(channel);
        }
        return channel;
    }

    @Override
    public Channel updateTopic(UUID id, String topic) {
        Channel channel = channelRepository.findById(id);
        if (channel != null) {
            channel.updateTopic(topic);
            channelRepository.save(channel);
        }
        return channel;
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}
