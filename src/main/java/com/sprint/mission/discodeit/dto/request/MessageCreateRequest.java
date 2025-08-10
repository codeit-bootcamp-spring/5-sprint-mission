package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MessageCreateRequest {
    private UUID userId;
    private UUID channelId;
    private String content;
    private List<UUID> attachmentIds;
}
