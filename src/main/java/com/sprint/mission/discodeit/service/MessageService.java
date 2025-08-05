package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMsg(String content, String channelId, String authorId);

    Message find(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String content, String channelId, String authorId);
    Message delete(UUID messageId);
}
