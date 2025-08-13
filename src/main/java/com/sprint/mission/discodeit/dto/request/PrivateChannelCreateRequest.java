package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotBlank List<UUID> userIds
) {
}
