package com.sprint.mission.discodeit.channel.domain.dto;

import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreatedEvent(
    Set<UUID> participantIds
) {
    public static final String TOPIC = "discodeit.channel.created.private";
}
