package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ChannelCategory;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private static final JCFChannelService instance = new JCFChannelService();

    private final List<Channel> data;

    private JCFChannelService() {
        data = new ArrayList<Channel>();
    }

    public static JCFChannelService getInstance() {
        return instance;
    }

    @Override
    public void createChannel(Channel channel) {
        boolean exists = data.stream()
                .anyMatch(c -> c.getId().equals(channel.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return;
        }
        data.add(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return data;
    }

    @Override
    public void updateName(Channel channel, String name) {
        data.stream()
                .filter(c -> c.getId().equals(channel.getId()))
                .findFirst()
                .ifPresent(c -> {
                    c.setName(name);
                    c.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateChannelCategory(Channel channel, ChannelCategory category) {
        data.stream()
                .filter(c -> c.getId().equals(channel.getId()))
                .findFirst()
                .ifPresent(c -> {
                    c.setCategory(category);
                    c.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsPublic(Channel channel, boolean isPublic) {
        data.stream()
                .filter(c -> c.getId().equals(channel.getId()))
                .findFirst()
                .ifPresent(c -> {
                    c.setPublic(isPublic);
                    c.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateAllowedUsers(Channel channel, List<User> allowedUsers) {
        data.stream()
                .filter(c -> c.getId().equals(channel.getId()))
                .findFirst()
                .ifPresent(c -> {
                    c.setAllowedUsers(allowedUsers);
                    c.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .ifPresent(data::remove);
    }
}
