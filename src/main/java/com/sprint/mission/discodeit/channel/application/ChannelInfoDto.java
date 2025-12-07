package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.ChannelType;

import java.util.UUID;

public record ChannelInfoDto(
    UUID id,
    ChannelType type,
    String name,
    String description
) {
}
