package com.sprint.mission.discodeit.channel.domain.dto;

import java.util.UUID;

public record ChannelDeletedEvent(
    UUID channelId
) {
    public static final String TOPIC = "discodeit.channel.deleted";
}
