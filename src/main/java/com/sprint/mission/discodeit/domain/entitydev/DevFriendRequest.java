package com.sprint.mission.discodeit.domain.entitydev;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"sender", "receiver"}, callSuper = false)
public class DevFriendRequest extends DevBaseEntity {

    private final UUID sender;
    private final UUID receiver;

    public DevFriendRequest(UUID sender, UUID receiver) {
        Objects.requireNonNull(sender, "Sender id must not be null.");
        Objects.requireNonNull(receiver, "Receiver id must not be null.");
        if (sender.equals(receiver)) throw new IllegalArgumentException("Cannot send friend request to self.");
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return String.format("FriendRequest[from=%s, to=%s]",
                sender, receiver);
    }
}
