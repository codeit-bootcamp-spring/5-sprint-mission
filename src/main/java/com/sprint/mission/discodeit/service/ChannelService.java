package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel);
    Channel get(UUID id);
    Channel get(String name);
    List<Channel> getAll();
    void update(Channel channel);
    void delete(UUID id);
}
