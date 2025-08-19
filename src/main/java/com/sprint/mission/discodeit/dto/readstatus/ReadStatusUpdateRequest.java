package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ReadStatusUpdateRequest {
    private UUID id; // 수정할 ReadStatus ID
    private Instant lastReadAt; // 읽은 시간
}
