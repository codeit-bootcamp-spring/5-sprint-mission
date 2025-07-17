package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    final Map<UUID, Channel> data = new HashMap<>();


    @Override
    public Channel createChannel(String channelName, String description) {
        Channel ch = new Channel(channelName, description);
        data.put(ch.getId(), ch);  // Map에 저장
        return ch;
    }

    @Override
    public Channel find(UUID channelId) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID chid, String channelName,String description) {
        Channel channel = data.get(chid);
        if(channel != null){
            channel.updateChannel(channelName, description);
        }
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID uuid) {
        return data.remove(uuid);
    }
}
