package com.sprint.mission.discodeit.channel.domain.dto;

import java.util.UUID;

public record PublicChannelUpdatedEvent(
    UUID channelId
) {
    public static final String TOPIC = "discodeit.channel.updated.private";
}
