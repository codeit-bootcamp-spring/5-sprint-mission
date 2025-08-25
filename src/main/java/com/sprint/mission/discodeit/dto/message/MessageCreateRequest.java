package com.sprint.mission.discodeit.dto.message;

import static com.sprint.mission.discodeit.support.Constants.MAX_MESSAGE_CONTENT_LENGTH;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(

    @Size(max = MAX_MESSAGE_CONTENT_LENGTH)
    String content,

    @NotNull
    UUID channelId,

    @NotNull
    UUID authorId
) {

}
