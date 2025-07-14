package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public void create(Channel channel) {
        channels.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        if  (channels.containsKey(id)) {
            return channels.get(id);
        }

        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void update(UUID id, String name, String description) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.update(name, description);
        }
    }

    @Override
    public boolean addUser(UUID channelId, UUID userId) {
        Channel channel = channels.get(channelId);
        if (channel == null) return false;
        return channel.addUser(userId);
    }

    @Override
    public boolean removeUser(UUID id, UUID userId) {
        Channel channel = channels.get(id);
        if (channel == null) return false;
        return channel.removeUser(userId);
    }

    @Override
    public boolean addMessage(UUID channelId, UUID messageId) {
        Channel channel = channels.get(channelId);
        if (channel == null) return false;
        channel.addMessage(messageId);
        return true;
    }

    @Override
    public boolean removeMessage(UUID id, UUID messageId) {
        Channel channel = channels.get(id);
        if (channel == null) return false;
        return channel.removeMessage(messageId);
    }


    @Override
    public void delete(UUID id) {
        channels.remove(id);
    }
}
