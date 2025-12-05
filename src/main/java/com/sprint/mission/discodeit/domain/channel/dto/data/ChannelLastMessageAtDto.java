package com.sprint.mission.discodeit.domain.channel.dto.data;

import java.time.Instant;
import java.util.UUID;

public record ChannelLastMessageAtDto(UUID channelId, Instant lastMessageAt) {
}
