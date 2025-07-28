package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private static final JCFChannelService instance = new JCFChannelService();
    private final Map<UUID, Channel> channels = new HashMap<>();
    private UserService userService;

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
           System.out.println(channel.getName() + "으로 입장합니다.");
       } else {
           return null;
       }
       return channel;
    }

    @Override
    public Channel findByName(String channelName) {
        for (Channel cName : channels.values()) {
            if (cName.getName().equals(channelName)) {
                return cName;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> list = new ArrayList<>(channels.values());
        for (Channel channel : list) {
            System.out.println(channel.getName());
        }
        return list;
    }

    @Override
    public void update(UUID channelId, String channelName) {
        Channel updateChannel = channels.get(channelId);
        if (channelId != null) {
            updateChannel.updateName(channelName);
        } else {
            System.out.println("해당 ID의 채널을 찾을 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID channelId) {
        channels.remove(channelId);
    }
}
