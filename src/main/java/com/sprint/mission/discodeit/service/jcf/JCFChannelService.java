package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel enter(UUID userId, UUID channelId) { // 미구현
        return null;
    }

    @Override
    public Channel leave(UUID userId, UUID channelId) { // 미구현
        return null;
    }

    @Override
    public Channel createChannel(String channelName, String channelIntroduction, int typeValue) {
        Channel channel = new Channel(channelName, channelIntroduction, typeValue);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannelName(UUID id, String newChannelName) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateChannelName(newChannelName);
            return channel;
        } else {
            return null;
        }
    }

    @Override
    public Channel updateChannelIntroduction(UUID id, String newChannelIntroduction) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateChannelIntroduction(newChannelIntroduction);
            return channel;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
