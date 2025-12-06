package com.sprint.mission.discodeit.channel.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "newName": null,
          "newDescription": "New channel description"
        }
        """
)
public record PublicChannelUpdateRequest(
    @Size(max = 100) String newName,
    String newDescription
) {
}
