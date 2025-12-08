package com.sprint.mission.discodeit.channel.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.sprint.mission.discodeit.channel.domain.Channel.DESCRIPTION_MAX_LENGTH;
import static com.sprint.mission.discodeit.channel.domain.Channel.NAME_MAX_LENGTH;

@Schema(
    example = """
        {
          "name": "Channel name",
          "description": null
        }
        """
)
public record PublicChannelCreateRequest(
    @NotBlank @Size(max = NAME_MAX_LENGTH) String name,
    @Size(max = DESCRIPTION_MAX_LENGTH) String description
) {
}
