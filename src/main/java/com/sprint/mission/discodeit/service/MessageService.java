package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Channel channel, String message, UUID author, boolean tts);
    Message findById(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String message, boolean allMentioned);
    Message deleteById(UUID messageId);
}
