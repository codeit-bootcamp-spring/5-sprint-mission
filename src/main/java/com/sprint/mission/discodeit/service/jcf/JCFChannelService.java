package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data = new ArrayList<>();

    public JCFChannelService() {}

    @Override
    public Channel createChannel(User user, String channelName, boolean nsfw) throws IllegalArgumentException, NullPointerException {
        if(user == null) {
            throw new NullPointerException("A channel object is empty.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank ");
        }

        Channel channel = new Channel(user, channelName, nsfw);
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

        throw new IllegalArgumentException("Channel not found.");
    }

    @Override
    public List<Channel> findAll() { return data; }

    @Override
    public Channel update(ChannelDTO channelDTO) throws NullPointerException, IllegalArgumentException {
        if(channelDTO.getId() == null) {
            throw new NullPointerException("channel id is null.");
        } if(channelDTO.getChannelName() == null || channelDTO.getChannelName().isBlank()) {
            throw new IllegalArgumentException("channel name is null or blank.");
        } if(channelDTO.getOwnerId() == null) {
            throw new NullPointerException("channel owner id is null.");
        }
        Iterator<Channel> iter = data.iterator();
        while (iter.hasNext()) {
            Channel channel = iter.next();
            if(channelDTO.getId().equals(channel.getId())) {
                try {
                    channel.update(channelDTO);
                } catch (IllegalArgumentException e) {
                    System.out.println((e.getMessage()));
                }
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

    @Override
    public ChannelDTO createChannelDTO(UUID channelId, String channelName, User owner, boolean nsfw) throws NullPointerException, IllegalArgumentException {
        if(channelId == null) {
            throw new NullPointerException("channel id is null.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank");
        } if(owner == null) {
            throw new NullPointerException("owner is null.");
        }

        return new ChannelDTO(channelId, channelName, owner, nsfw);
    }

}
