package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {

    private final UUID userId;
    private final UUID channelId;

    private UUID lastReadMessageId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
    }

    public void read(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId must not be null");
        if (!Objects.equals(this.lastReadMessageId, messageId)) {
            this.lastReadMessageId = messageId;
            touch();
        }
    }

    public void setLastReadMessageId(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId must not be null");
        this.lastReadMessageId = messageId;
    }

    @Override
    public String toString() {
        return "ReadStatus[userId=%s, channelId=%s, lastReadMessageId=%s]"
                .formatted(userId, channelId, lastReadMessageId);
    }
}
