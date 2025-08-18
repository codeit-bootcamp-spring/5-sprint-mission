package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record MessageSendRequest(

    @NotNull
    UUID senderId,

    @NotBlank
    String content,

    Set<UUID> attachmentIds,

    UUID replyTo
) {

}
