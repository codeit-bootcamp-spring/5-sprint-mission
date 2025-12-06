package com.sprint.mission.discodeit.domain.channel.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record ChannelLastMessageAtDto(
    UUID channelId,
    Instant lastMessageAt
) {
}
