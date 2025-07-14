package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels;

    public JCFChannelService() {
        channels = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                System.out.println("중복된 이름의 채널이 있습니다.");
                return null;
            }
        }
        Channel channel = new Channel(name);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(String name) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                return ch;
            }
        }
        return null;
    }

    @Override
    public List<Channel> getChannels() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public boolean updateChannel(UUID uuid, String name) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                System.out.println("채널명 중복");
                return false;
            }
        }
        Channel channel = channels.get(uuid);
        channel.setName(name);
        channels.put(uuid, channel);
        return true;
    }

    @Override
    public boolean deleteChannel(UUID uuid) {
        if (channels.containsKey(uuid)) {
            channels.remove(uuid);
            return true;
        }
        return false;
    }
}
