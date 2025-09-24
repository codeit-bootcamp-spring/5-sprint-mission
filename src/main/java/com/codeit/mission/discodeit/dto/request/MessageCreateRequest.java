package com.codeit.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {

}
