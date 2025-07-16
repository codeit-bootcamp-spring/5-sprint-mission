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
    public Channel create(String name, String description) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("중복된 이름입니다.");
            }
        }
        Channel channel = new Channel(name, description);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel get(String name) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                return ch;
            }
        }
        throw new IllegalArgumentException("존재하지 않습니다.");
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel updateName(UUID uuid, String name) {
        for (Channel ch : channels.values()) {
            if (ch.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("중복된 이름입니다.");
            }
        }
        Channel channel = channels.get(uuid);
        channel.setName(name);
        return channel;
    }

    @Override
    public Channel updateDescription(UUID uuid, String description) {
        if (!channels.containsKey(uuid)) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("채널 설명입력해주세요");
        }
        Channel channel = channels.get(uuid);
        channel.setDescription(description);
        return channel;
    }

    @Override
    public boolean delete(UUID uuid) {
        if (channels.containsKey(uuid)) {
            channels.remove(uuid);
            return true;
        }
        return false;
    }
}
