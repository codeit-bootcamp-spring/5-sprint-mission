package com.sprint.mission.discodeit.service.core;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChannelManageService {

    private final ChannelService channelService;
    private final UserService userService;

    public ChannelManageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    public void addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = channelService.get(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("채널 없음");
        }

        if (channel.getUserIds().contains(userId)) {
            return;
        }

        channel.addUser(userId);
    }

    public void removeUserFromChannel(UUID channelId, UUID userId) {
        Channel channel = channelService.get(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("채널 없음");
        }

        if (channel.getUserIds().stream().noneMatch(id -> id.equals(userId))) {
            throw new IllegalArgumentException("해당 유저 없음");
        }

        channel.removeUser(userId);
        // TODO adminUserId가 나가는 케이스
    }

    public List<User> listUsersInChannel(UUID channelId) {
        Channel channel = channelService.get(channelId);

        if (channel == null) {
            return List.of();
        }

        return channel.getUserIds().stream()
            .map(userService::get)
            .filter(Objects::nonNull)
            .toList();
    }

    public List<Channel> findChannelsByUser(UUID userId) {
        return channelService.getAll().stream()
            .filter(c -> c.getUserIds().contains(userId))
            .toList();
    }
}