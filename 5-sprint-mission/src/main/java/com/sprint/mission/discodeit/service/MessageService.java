package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {


    Message create(String content, UUID authorId, UUID channelId);
    Message delete(UUID id);
    Message update(UUID id, String content);
    Message find(UUID id);
    List<Message> findAll();
}
