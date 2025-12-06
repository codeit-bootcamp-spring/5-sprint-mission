package com.sprint.mission.discodeit.binarycontent.presentation.dto;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
    example = """
        {
          "id": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
          "fileName": "profile.png",
          "size": 24123,
          "contentType": "image/png",
          "status": "PROCESSING"
        }
        """
)
public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status
) {
}
