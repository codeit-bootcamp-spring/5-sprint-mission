package com.sprint.mission.discodeit.dto.neutral;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewBinaryContent(
    @NotBlank String fileName,
    @NotNull String contentType,
    @NotNull byte[] bytes
) {

}
