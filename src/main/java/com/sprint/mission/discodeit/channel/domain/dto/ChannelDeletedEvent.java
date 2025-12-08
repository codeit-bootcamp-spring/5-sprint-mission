package com.sprint.mission.discodeit.channel.domain.dto;

import com.sprint.mission.discodeit.channel.domain.ChannelType;

import java.util.UUID;

public record ChannelDeletedEvent(
    UUID channelId,
    ChannelType channelType
) {
    public static final String TOPIC = "discodeit.channel.deleted";
}
