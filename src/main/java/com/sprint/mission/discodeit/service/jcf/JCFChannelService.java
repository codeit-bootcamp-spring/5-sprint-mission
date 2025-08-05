package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        data = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(UUID userId, String channelName, ChannelType channelType, boolean nsfw) throws IllegalArgumentException, NullPointerException {
        try {
            userService.findById(userId);
        } catch (Exception e) {
            throw e;
        }
        if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank.");
        } if(channelType == null) {
            throw new IllegalArgumentException("channelType is null.");
        }

        Channel channel = new Channel(userId, channelName, channelType, nsfw);
        data.add(channel);

        return channel;
    }

    @Override
    public Channel findById(UUID channelId) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        }
        for(Channel channel : data) {
            if(channel.getId().equals(channelId)) {
                return channel;
            }
        }

        throw new IllegalArgumentException("Channel(" + channelId + ")not found.");
    }

    @Override
    public List<Channel> findAll() { return data; }

    @Override
    public Channel update(UUID channelId, UUID ownerId, String channelName, boolean nsfw) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channel name is null or blank.");
        } if(ownerId == null) {
            throw new NullPointerException("channel owner id is null.");
        }
        Iterator<Channel> iter = data.iterator();

        while (iter.hasNext()) {
            Channel channel = iter.next();
            if(channelId.equals(channel.getId())) {
                channel.update(ownerId, channelName, nsfw);
                return channel;
            }
        }

        throw new IllegalArgumentException("Channel not found.");
    }

    @Override
    public Channel deleteById(UUID channelId) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        }

        Iterator<Channel> iter = data.iterator();
        while(iter.hasNext()) {
            Channel channel = iter.next();
            if(channel.getId().equals(channelId)) {
                data.remove(channel);
                return channel;
            }
        }
        throw new IllegalArgumentException("Channel id is wrong.");
    }

}
