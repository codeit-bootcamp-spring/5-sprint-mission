package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateRequest(
    @NotNull String name,
    String description
) {

}
