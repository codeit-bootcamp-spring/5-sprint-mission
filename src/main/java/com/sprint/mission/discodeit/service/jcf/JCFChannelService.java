package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private static final Map<UUID,Channel> data = new HashMap<>();
    private static JCFChannelService instance;

    private JCFChannelService() {}

    public static JCFChannelService getInstance() {
        if (instance == null) {
            instance = new JCFChannelService();
        }
        return instance;
    }

    @Override
    public void add(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findOne(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID channelId, Channel channel) {
        Channel origin = data.remove(channelId);
        origin.updateChannelName(channel.getChannelName());
        data.put(origin.getId(), origin);
    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
    }
    @Override
    public void deleteAll() {
        data.clear();
    }

}
