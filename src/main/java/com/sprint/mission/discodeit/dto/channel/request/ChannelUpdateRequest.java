package com.sprint.mission.discodeit.dto.channel.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ChannelUpdateRequest(
        @NotNull(message = "아이디를 입력해주세요")
        UUID id,
        ChannelType channelType,
        @Nullable String name,
        @Nullable String topic,
        @Nullable String description
) {}
