package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record ChannelCreateByGuildRequest(
        @NotNull UUID guildId,
        @NotBlank String name,
        @NotNull ChannelType type,
        Boolean isSecret,
        Set<UUID> allowedMemberIds
) {
}
