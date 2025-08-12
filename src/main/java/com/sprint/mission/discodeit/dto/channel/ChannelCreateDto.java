package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record ChannelCreateDto(
        @NotBlank(message = "이름을 입력하세요")
        String name,

        @NotNull(message = "채널 타입을 입력하세요")
        ChannelType type,
        @Nullable String topic,
        @Nullable String description
) {}
