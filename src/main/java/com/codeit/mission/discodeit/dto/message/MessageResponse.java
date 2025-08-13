package com.codeit.mission.discodeit.dto.message;

import com.codeit.mission.discodeit.entity.Message;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class MessageResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;

    public MessageResponse(Message message, List<UUID> attachmentIds) {
        this.id = message.getId();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
        this.content = message.getContent();
        this.channelId = message.getChannelId();
        this.authorId = message.getAuthorId();
        this.attachmentIds = attachmentIds;
    }

    public MessageResponse(Message message) {
        this(message, null);
    }
}
