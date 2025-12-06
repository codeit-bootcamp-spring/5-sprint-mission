package com.sprint.mission.discodeit.channel.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record ChannelLastMessageAtDto(
    UUID channelId,
    Instant lastMessageAt
) {
}
