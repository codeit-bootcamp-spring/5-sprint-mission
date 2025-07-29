package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private static final Map<UUID,Channel> data = new HashMap<>();

    public JCFChannelService() {}

    @Override
    public void add(Channel channel) {
        if(channel == null){
            throw new IllegalArgumentException("channel은 null일 수 없다.");
        }
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findOne(UUID channelId) {
        if(channelId == null){
            throw new IllegalArgumentException("channelId은 null일 수 없다.");
        }
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID channelId, Channel channel) {
        if(channelId == null || channel == null){
            throw new IllegalArgumentException("channelId 혹은 channel은 null일 수 없다");
        }

        Channel origin = data.remove(channelId);
        origin.updateChannelName(channel.getChannelName());
        data.put(origin.getId(), origin);
    }

    @Override
    public void delete(UUID channelId) {
        if(channelId == null){
            throw new IllegalArgumentException("channelId은 null일 수 없다.");
        }
        data.remove(channelId);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }



}
