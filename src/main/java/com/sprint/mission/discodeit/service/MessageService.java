package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Message;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MessageService {
    Message send(UUID chatRoom, UUID senderId, String content, Set<String> files, UUID replyTo);

    void updateContent(UUID messageId, String content);

    void updateFiles(UUID messageId, List<String> files);

    void printSenderAndContent(UUID messageId);
}
