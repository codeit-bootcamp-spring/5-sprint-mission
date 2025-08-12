package com.sprint.mission.discodeit.service.dto.message;

import java.util.UUID;

public class MessageUpdateRequest {
    private final UUID messageId;
    private final String newContent;

    public MessageUpdateRequest(UUID messageId, String newContent) {
        this.messageId = messageId;
        this.newContent = newContent;
    }
    public UUID getMessageId() { return messageId; }
    public String getNewContent() { return newContent; }
}

