package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ChannelCreateRequest(
        @NotBlank String name,
        String description,
        List<UUID> userIds
) {
}
