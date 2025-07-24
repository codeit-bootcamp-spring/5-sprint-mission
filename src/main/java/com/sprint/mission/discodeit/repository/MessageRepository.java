package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Map;
import java.util.UUID;

public interface MessageRepository {

    void save(Map<UUID, Message> data);
    Map<UUID, Message> loadData();
    void clear();

}
