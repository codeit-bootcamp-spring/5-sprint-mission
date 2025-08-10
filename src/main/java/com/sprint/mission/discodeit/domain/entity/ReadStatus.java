package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID userId;
    private final UUID chatRoomId;

    private boolean read;

    public ReadStatus(UUID userId, UUID chatRoomId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.chatRoomId = Objects.requireNonNull(chatRoomId, "chatRoomId must not be null");
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

    @Override
    public String toString() {
        return String.format(
                "ReadStatus[userId=%s, chatRoomId=%s, read=%s]",
                userId, chatRoomId, read
        );
    }
}
