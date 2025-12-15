package com.sprint.mission.discodeit.message.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import static com.sprint.mission.discodeit.message.domain.Message.CONTENT_MAX_LENGTH;

@Schema(
    example = """
        {
          "newContent": ""
        }
        """
)
public record MessageUpdateRequest(
    @Size(max = CONTENT_MAX_LENGTH) String newContent
) {
}
