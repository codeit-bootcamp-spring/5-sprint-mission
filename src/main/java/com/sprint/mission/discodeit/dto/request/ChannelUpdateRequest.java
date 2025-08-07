package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID id,
        @NotBlank String name,
        String description
) {
}
