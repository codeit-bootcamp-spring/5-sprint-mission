package com.sprint.mission.discodeit.channel.domain;

import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreatedEvent(
    Set<UUID> participantIds
) {
    public static final String TOPIC = "discodeit.channel.created.private";
}
