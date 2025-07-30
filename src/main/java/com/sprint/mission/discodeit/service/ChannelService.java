package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(String name);
    Optional<Channel> findById(UUID id);
    Optional<Channel> findByName(String name);
    boolean addUserToChannel(UUID channelID, UUID userID);
    boolean removeUserToChannel(UUID channelID, UUID userID);
    List<Channel> findAll();
    void updatedName(UUID id, String name);
    void delete(UUID id);
}
