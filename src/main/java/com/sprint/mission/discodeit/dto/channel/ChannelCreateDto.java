package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelCreateDto(String name,
                               ChannelType type,
                               String description) {
}
