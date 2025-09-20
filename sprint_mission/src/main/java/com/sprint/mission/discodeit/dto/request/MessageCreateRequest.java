package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메세지를 입력하세요")
    String content,
    UUID channelId,
    UUID authorId
) {

}
