package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, UUID authorId, String description);

    Channel find(UUID id);

    List<Channel> findAll();

    List<Channel> searchByName(String token);

    Channel update(UUID id, UUID requestId, String newName, String newDescription);

    void delete(UUID id, UUID requestId);
}
