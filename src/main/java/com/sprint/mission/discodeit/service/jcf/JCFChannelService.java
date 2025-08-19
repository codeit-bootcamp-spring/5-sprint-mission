package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {


    final Map<UUID, Channel> data = new HashMap<>();


    @Override
    public Channel createChannel(ChannelType type, String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("name is null");
        }
        Channel channel = new Channel(type, name);
        data.put(channel.getId() , channel);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        if(!data.containsKey(channelId)){
            throw new NoSuchElementException("channel not found");
        }
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelId,ChannelType type, String name) {
        if (name == null || name.isBlank() || type == null) {
            throw new IllegalArgumentException("name is null or blank");
        }

        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("channel not found");
        }
        Channel updatedChannel = new Channel(type, name);
        data.put(channelId, updatedChannel);
        return updatedChannel;
    }

    @Override
    public Channel delete(UUID channelId) {
            Channel channel = data.remove(channelId);
            if(channel == null){
                throw new NoSuchElementException("channel not found");
            }
            return channel;
        }
}
