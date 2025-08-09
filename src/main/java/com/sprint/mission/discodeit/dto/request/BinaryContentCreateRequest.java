package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.nio.file.Path;

@Builder
public record BinaryContentCreateRequest(
        @NotNull Path path,
        @NotBlank String fileName,
        @NotNull String contentType
) {
}
