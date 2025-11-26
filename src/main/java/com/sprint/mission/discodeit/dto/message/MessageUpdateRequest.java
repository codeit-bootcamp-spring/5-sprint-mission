package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    example = """
        {
          "newContent": ""
        }
        """
)
public record MessageUpdateRequest(
    String newContent
) {
}
