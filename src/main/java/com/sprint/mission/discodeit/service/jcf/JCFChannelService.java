package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(ChannelType type, String name, UUID authorId, String description) {
        try {
            userService.find(authorId);
        } catch (NoSuchElementException e) {
            throw e;
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("[!] 값이 null 이거나 비어있을 수 없습니다.");
        }
        for (Channel channel : data.values()) {
            if (channel.getName().equals(name)) {
                throw new IllegalArgumentException("[!] 이름이 중복입니다.");
            }
        }
        Channel channel = new Channel(type, name, authorId, description);
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public Channel find(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("[!] 채널이 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("[!] 결과가 없습니다.");
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> searchByName(String token) {
        return data.values().stream().filter(c -> (c.getName().contains(token))).toList();
    }

    @Override
    public Channel update(UUID id, UUID requestId, String newName, String newDescription) {
        Channel channel = data.get(id);
        if (!channel.getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 수정 권한이 없습니다.");
        }
        channel.update(newName, newDescription);
        return channel;
    }

    @Override
    public void delete(UUID id, UUID requestId) {
        Channel channel = data.get(id);
        if (!channel.getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 삭제 권한이 없습니다.");
        }
        data.remove(id);
    }
}
