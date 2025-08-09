package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MessageService {
    UUID send(UUID senderId, UUID receiverId, String content, Set<String> files, UUID replyTo);

    void updateContent(UUID messageId, String content);

    void updateFiles(UUID messageId, List<String> files);

    void printSenderAndContent(UUID messageId);
}
