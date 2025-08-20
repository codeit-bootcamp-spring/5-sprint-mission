package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.CreateFile;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        UUID channelId,
        UUID senderId,
        String content,
        List<CreateFile> attachments
) {}
