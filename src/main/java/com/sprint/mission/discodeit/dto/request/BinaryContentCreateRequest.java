package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank @Size(max = 255) String fileName,
    @NotNull @Size(max = 10_000_000, message = "파일은 최대 10MB까지 허용됩니다.") byte[] bytes,
    @NotBlank String contentType
) {}
