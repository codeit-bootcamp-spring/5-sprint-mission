package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public final class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private static final JCFChannelService instance = new JCFChannelService();

    private JCFChannelService() {
        this.data = new HashMap<>();
    }

    public static JCFChannelService getInstance() {
        return instance;
    }

    @Override
    public void create(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel get(UUID id) {
        return data.get(id);
    }

    @Override
    public Channel get(String name) {
        if(name == null || name.isBlank()) return null;
        for(Channel channel : data.values()) {
            if(channel.getName().equals(name)){
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Channel channel) {
        if (data.containsKey(channel.getId())) {
            data.put(channel.getId(), channel);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
