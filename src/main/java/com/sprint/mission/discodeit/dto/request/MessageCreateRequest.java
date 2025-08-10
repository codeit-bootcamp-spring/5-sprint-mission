package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContent> attachments
) {}
