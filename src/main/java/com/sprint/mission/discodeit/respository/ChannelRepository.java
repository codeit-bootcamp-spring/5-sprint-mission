package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    Channel save(Channel channel);

    List<Channel> findAll();

    Optional<Channel> findById(UUID id);

    List<Channel> findByName(String name);

    Optional<Channel> updateName(UUID id, String name);

    Optional<Channel> updateTopic(UUID id, String topic);

    boolean delete(UUID id);
}
