package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class MessageResponse {
    private final UUID id;
    private final UUID channelId;
    private final UUID authorId;
    private final String content;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.channelId = message.getChannelId();
        this.authorId = message.getAuthorId();
        this.content = message.getContent();
        this.createdAt = Instant.ofEpochSecond(message.getCreatedAt());
        this.updatedAt = Instant.ofEpochSecond(message.getUpdatedAt());
    }
}
