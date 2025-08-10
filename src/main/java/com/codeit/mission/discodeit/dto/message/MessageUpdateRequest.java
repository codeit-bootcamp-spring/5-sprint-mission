package com.codeit.mission.discodeit.dto.message;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class MessageUpdateRequest {

    private final UUID messageId;
    private final String content;

    public MessageUpdateRequest(UUID messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }
}
