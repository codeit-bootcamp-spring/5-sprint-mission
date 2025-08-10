package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant createdAt;
    private List<UUID> attachmentIds;
}
