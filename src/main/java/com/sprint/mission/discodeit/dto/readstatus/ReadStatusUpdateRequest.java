package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ReadStatusUpdateRequest {

  private UUID id; // 수정할 ReadStatus ID
  private Instant newLastReadAt; // 읽은 시간
}
