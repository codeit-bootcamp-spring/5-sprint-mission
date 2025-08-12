package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class MessageResponse {
    private final UUID id;
    private final UUID channelId;
    private final UUID authorId;
    private final String content;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponse(Message message) {
        this.id        = message.getId();
        this.channelId = message.getChannelId();
        this.authorId  = message.getUserId();
        this.content   = message.getContent();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }
}
