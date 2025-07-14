package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private static final JCFChannelService instance = new JCFChannelService();
    private final Map<UUID, Channel> channels = new HashMap<>();

    private JCFChannelService() {}

    public static JCFChannelService getInstance() {
        return instance;
    }

    @Override
    public void create(Channel channel) {
        channels.putIfAbsent(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID channelId) {
       Channel channel = channels.get(channelId);
       if (channel != null) {
           System.out.println(channel);
       } else {
           System.out.println("채널을 찾을 수 없습니다.");
       }
       return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> list = new ArrayList<>(channels.values());
        for (Channel channel : list) {
            System.out.println(channel);
        }
        return list;
    }

    @Override
    public void update(UUID channel, String channelName) {
        Channel updateChannel = channels.get(channel);
        if (channel != null) {
            updateChannel.updateName(channelName);
        }
    }

    @Override
    public void delete(String channelName) {
        channels.remove(channelName);
    }
}
