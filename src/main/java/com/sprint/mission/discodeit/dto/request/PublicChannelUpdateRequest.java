package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters") String newName,

    @Size(max = 200, message = "Description must be at most 200 characters") String newDescription) {

}
