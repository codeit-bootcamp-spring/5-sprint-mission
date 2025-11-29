package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "name": "Channel name",
          "description": "Channel description"
        }
        """
)
public record PublicChannelCreateRequest(
    @NotBlank
    @Size(max = 100)
    String name,
    String description
) {
}
