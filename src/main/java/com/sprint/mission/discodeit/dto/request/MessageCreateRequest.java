package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
    String content,
    /*@NotNull*/ UUID channelId,
    /*@NotNull*/ UUID authorId
) {

}
