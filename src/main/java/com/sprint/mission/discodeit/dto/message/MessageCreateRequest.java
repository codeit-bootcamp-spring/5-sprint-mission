package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(
    example = """
        {
          "content": "Hello, world!",
          "channelId": "cce7f6a2-f709-4d43-a234-b18c5f43b662",
          "authorId": "4efc344f-350d-48b0-893e-320ef5f8ae61"
        }
        """
)
public record MessageCreateRequest(
    String content,
    @NotNull UUID channelId,
    @NotNull UUID authorId
) {

}
