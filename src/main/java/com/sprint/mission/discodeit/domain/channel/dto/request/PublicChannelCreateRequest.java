package com.sprint.mission.discodeit.domain.channel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "name": "Channel name",
          "description": null
        }
        """
)
public record PublicChannelCreateRequest(
    @NotBlank @Size(max = 100) String name,
    String description
) {
}
