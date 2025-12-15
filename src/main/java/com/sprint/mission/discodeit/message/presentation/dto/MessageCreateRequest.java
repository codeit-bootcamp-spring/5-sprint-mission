package com.sprint.mission.discodeit.message.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static com.sprint.mission.discodeit.message.domain.Message.CONTENT_MAX_LENGTH;

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
    @Size(max = CONTENT_MAX_LENGTH) String content,
    @NotNull UUID channelId,
    @NotNull UUID authorId
) {
}
