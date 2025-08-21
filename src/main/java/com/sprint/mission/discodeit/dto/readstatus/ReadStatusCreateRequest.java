package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ReadStatusCreateRequest {

  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;
}
