package com.sprint.mission.discodeit.domain.message.dto;

import java.util.UUID;

public record MessageCreateRequest(
    String content,
    UUID channelId,
    UUID authorId
) {

}
