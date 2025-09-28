package com.sprint.mission.discodeit.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
    @NotBlank(message = "Content is mandatory") @Size(max = 200, message = "Content must be at most 200 characters") String newContent) {

}
