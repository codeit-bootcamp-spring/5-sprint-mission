package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Data;

import java.util.UUID;

@Data
public class ReadStatusCreateRequest {
    private UUID channelId;
    private UUID userId;
}
