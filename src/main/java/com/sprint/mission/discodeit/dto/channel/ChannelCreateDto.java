package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.lang.Nullable;

public record ChannelCreateDto(
        String name,
        ChannelType type,
        @Nullable String topic,
        @Nullable String description
) {}
