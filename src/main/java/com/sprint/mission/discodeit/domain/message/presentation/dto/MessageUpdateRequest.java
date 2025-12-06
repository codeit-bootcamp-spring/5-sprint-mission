package com.sprint.mission.discodeit.domain.message.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "newContent": ""
        }
        """
)
public record MessageUpdateRequest(@Size(max = 4000) String newContent) {
}
