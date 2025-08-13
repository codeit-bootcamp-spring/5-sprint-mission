package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record ChannelUpdateByGuildRequest(
        @NotBlank String name,
        ChannelType type,
        Set<UUID> allowedMemberIds
) {
}
