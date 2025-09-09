package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
    example = """
        {
          "name": "Channel name",
          "description": "Channel description"
        }
        """
)
public record PublicChannelCreateRequest(@NotBlank String name, String description) {

}
