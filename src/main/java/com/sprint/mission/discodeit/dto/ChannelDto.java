package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.lang.Nullable;

public class ChannelDto {

    public record Create(
        String name,
        ChannelType type,
        @Nullable String topic,
        @Nullable String description
    ) {}
}
