package com.sprint.mission.discodeit.domain.deventity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"sender", "receiver"}, callSuper = false)
public class DevFriendRequest extends DevBaseEntity {

    private final UUID sender;
    private final UUID receiver;

    public DevFriendRequest(UUID sender, UUID receiver) {
        validateUsers(sender, receiver);
        this.sender = sender;
        this.receiver = receiver;
    }

    public static void validateUsers(UUID sender, UUID receiver) {
        if (sender == null || receiver == null)
            throw new IllegalArgumentException("Sender id and receiver id must not be null.");
        if (sender.equals(receiver)) throw new IllegalArgumentException("Cannot send friend request to self.");
    }

    @Override
    public String toString() {
        return String.format("FriendRequest[from=%s, to=%s]",
                sender, receiver);
    }
}
