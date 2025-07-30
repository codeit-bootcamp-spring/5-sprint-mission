package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, UUID channelId, UUID authorId);

    Optional<Message> getMessage(UUID id);

    List<Message> getAllMessages();

    Message updateMessage(UUID id, String content);

    Message deleteMessage(UUID id);

}
