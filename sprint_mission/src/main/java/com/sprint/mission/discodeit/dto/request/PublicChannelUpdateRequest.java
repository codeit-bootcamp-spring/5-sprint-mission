package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
