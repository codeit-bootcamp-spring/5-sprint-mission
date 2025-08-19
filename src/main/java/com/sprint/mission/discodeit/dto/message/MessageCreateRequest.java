package com.sprint.mission.discodeit.dto.message;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MessageCreateRequest {
    private String content;
    private UUID channelId;
    private UUID sender;
    private List<UUID> attachmentIds;

}
