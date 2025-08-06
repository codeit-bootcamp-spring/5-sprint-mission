package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(UUID userId,
                               UUID channelId,
                               String content,
                               @Nullable List<BinaryContent> filesId) {
}
