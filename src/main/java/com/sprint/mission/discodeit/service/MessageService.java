package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void updateContent(UUID messageId, String content);

    void updateFiles(UUID messageId, List<String> files);

    void printSenderAndContent(UUID messageId);
}
