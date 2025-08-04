package com.sprint.mission.discodeit.dto;

import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class MessageDto {

    public record Create(
            UUID userId,
            UUID channelId,
            String content,
            @Nullable
            List<UUID> fileIds
    ) {}
}
