package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class FriendRequest extends BaseEntity {

    public enum Status {
        PENDING, ACCEPTED, REJECTED, CANCELED, EXPIRED
    }

    private final UUID senderId;
    private final UUID receiverId;

    public FriendRequest(UUID senderId, UUID receiverId) {
        this.senderId = Objects.requireNonNull(senderId, "senderId must not be null.");
        this.receiverId = Objects.requireNonNull(receiverId, "receiverId must not be null.");
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to self.");
        }
    }

    public void accept(UUID actorId) {
        requireReceiver(actorId);
        touch();
    }

    public void reject(UUID actorId) {
        requireReceiver(actorId);
        touch();
    }

    public void cancel(UUID actorId) {
        requireSender(actorId);
        touch();
    }

    private void requireSender(UUID actorId) {
        if (!this.senderId.equals(Objects.requireNonNull(actorId, "actorId must not be null"))) {
            throw new IllegalStateException("Only sender can perform this action.");
        }
    }

    private void requireReceiver(UUID actorId) {
        if (!this.receiverId.equals(Objects.requireNonNull(actorId, "actorId must not be null"))) {
            throw new IllegalStateException("Only receiver can perform this action.");
        }
    }

    @Override
    public String toString() {
        return "FriendRequest[id=%s, from=%s, to=%s]"
                .formatted(getId(), senderId, receiverId);
    }
}
