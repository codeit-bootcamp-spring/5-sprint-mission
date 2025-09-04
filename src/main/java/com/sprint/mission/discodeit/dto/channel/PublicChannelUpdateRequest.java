package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    example = """
        {
          "newName": "New channel name",
          "newDescription": "New channel description"
        }
        """
)
public record PublicChannelUpdateRequest(String newName, String newDescription) {

}
