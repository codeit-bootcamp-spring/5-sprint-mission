package com.sprint.mission.discodeit.dto.sse;

import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
public class SseMessage {
    private final UUID eventId;
    private final String eventName;
    private final Object data;
    private final Set<UUID> receiverIds;
    private final boolean broadcast;

    private SseMessage(Set<UUID> receiverIds, String eventName, Object data, boolean broadcast) {
        this.eventId = UUID.randomUUID();
        this.eventName = eventName;
        this.data = data;
        this.receiverIds = receiverIds;
        this.broadcast = broadcast;
    }

    public static SseMessage createPrivate(Collection<UUID> receiverIds, String eventName, Object data) {
        return new SseMessage(Set.copyOf(receiverIds), eventName, data, false);
    }

    public static SseMessage createBroadcast(String eventName, Object data) {
        return new SseMessage(Set.of(), eventName, data, true);
    }

    public boolean isReceivable(UUID receiverId) {
        return broadcast || receiverIds.contains(receiverId);
    }
}
