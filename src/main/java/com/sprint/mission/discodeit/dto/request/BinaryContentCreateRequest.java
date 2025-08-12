package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.nio.file.Path;

public record BinaryContentCreateRequest(
        @NotBlank String fileName,
        @NotNull String contentType,
        @NotNull byte[] bytes
) {
}
