package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;


public class JCFChannelService implements ChannelService {

    final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel createChannel(String channelname) {
        Channel channel = new Channel(channelname);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID channelId) {
        if(!data.containsKey(channelId)){
            throw new NoSuchElementException("channel not found");
        }
        return data.get(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelname) {
        Channel channel = getChannel(channelId);
        channel.update(channelname);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        if(!data.containsKey(channelId)){
            throw new NoSuchElementException("channel not found");
        }
        data.remove(channelId);
    }
}
