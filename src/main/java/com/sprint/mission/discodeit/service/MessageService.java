package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.main.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(UUID authorId, UUID channelId, String content);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, String content);
    void delete(UUID id);
}
