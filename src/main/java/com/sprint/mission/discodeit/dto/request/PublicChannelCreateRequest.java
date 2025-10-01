package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(@NotBlank(message = "Name is mandatory") String name,
                                         @Size(max = 200, message = "Description must be at most 200 characters") String description) {

}
