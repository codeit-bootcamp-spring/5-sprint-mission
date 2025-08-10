package com.sprint.mission.discodeit.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"senderId", "receiverId"}, callSuper = false)
public class FriendRequest extends BaseEntity {

    private final UUID senderId;
    private final UUID receiverId;

    public FriendRequest(UUID senderId, UUID receiverId) {
        Objects.requireNonNull(senderId, "Sender id must not be null.");
        Objects.requireNonNull(receiverId, "Receiver id must not be null.");
        if (senderId.equals(receiverId)) throw new IllegalArgumentException("Cannot send friend request to self.");
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return String.format("FriendRequest[from=%s, to=%s]",
                senderId, receiverId);
    }
}
