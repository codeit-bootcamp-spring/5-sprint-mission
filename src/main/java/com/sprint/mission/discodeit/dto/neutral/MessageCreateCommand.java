package com.sprint.mission.discodeit.dto.neutral;

import java.util.List;
import java.util.UUID;

public record MessageCreateCommand(
    UUID channelId,
    UUID authorId,
    String content,
    List<NewBinaryContent> attachments
) {

}
