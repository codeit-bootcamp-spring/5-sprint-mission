package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PublicChannelUpdateRequest(
    @NotNull UUID id,
    @NotBlank String name,
    String description
) {

}
