package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel) throws IOException;
    Channel findById(UUID id) throws IOException, ClassNotFoundException;
    Channel findByName(String name) throws IOException, ClassNotFoundException;
    List<Channel> findAll() throws IOException, ClassNotFoundException;
    void update(Channel channel) throws IOException;
    void delete(UUID id) throws IOException;
}
