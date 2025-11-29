package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "newName": "New channel name",
          "newDescription": "New channel description"
        }
        """
)
public record PublicChannelUpdateRequest(
    @Size(max = 100)
    String newName,
    String newDescription
) {
}
