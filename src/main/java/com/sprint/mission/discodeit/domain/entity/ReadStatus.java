package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {

    private final UUID userId;
    private final UUID channelId;

    private boolean read;
    private UUID lastReadMessageId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
    }

    public void markAsRead() {
        if (!this.read) {
            this.read = true;
            touch();
        }
    }

    public void markAsUnread() {
        if (this.read) {
            this.read = false;
            touch();
        }
    }

    public void updateLastReadMessageId(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId must not be null");
        if (!Objects.equals(this.lastReadMessageId, messageId)) {
            this.lastReadMessageId = messageId;
            if (!this.read) this.read = true;
            touch();
        }
    }

    public void clearLastReadMessageId() {
        if (this.lastReadMessageId != null) {
            this.lastReadMessageId = null;
            touch();
        }
    }

    @Override
    public String toString() {
        return "ReadStatus[userId=%s, channelId=%s, read=%s, lastReadMessageId=%s]"
                .formatted(userId, channelId, read, lastReadMessageId);
    }
}
