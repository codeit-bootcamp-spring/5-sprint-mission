package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public JCFChannelService() {
        this.channelRepository = new JCFChannelRepository();
    }

    @Override
    public Channel createChannel(String channelName, String channelDescription) {
        Channel channel = new Channel(UUID.randomUUID(), channelName, channelDescription, Instant.now().getEpochSecond());
        channelRepository.save(channel);

        return channel;
    }

    @Override
    public Channel getById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> getByChannelName(String channelName) {
        return channelRepository.findByName(channelName);
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateById(UUID channelId, String channelName, String channelDescription) {
        List<Channel> channels = getAll();
        for (Channel channel : channels) {
            if (channel.getId().equals(channelId)) {
                channel.updateChannel(channelName, channelDescription, Instant.now().getEpochSecond());

                return channel;
            }
        }
        return null;
    }

    @Override
    public boolean removeById(UUID channelId) {
        return channelRepository.delete(channelId);
    }
}
