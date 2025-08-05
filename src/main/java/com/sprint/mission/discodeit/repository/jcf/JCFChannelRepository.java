package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> channels;

    public JCFChannelRepository() {
        this.channels = new ArrayList<>();
    }

    @Override
    public void save(Channel channel) {
        channels.add(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findByName(String channelName) {
        return channels.stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAll() {
        return channels;
    }

    @Override
    public void update(UUID id, Channel updatedChannel) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(id)) {
                channels.set(i, updatedChannel);
                break;
            }
        }
    }

    @Override
    public boolean delete(UUID id) {
        Channel channel = findById(id);
        return channel != null && channels.remove(channel);
    }
}
