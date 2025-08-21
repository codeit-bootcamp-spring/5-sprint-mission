package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class UserStatusCreateRequest {

  private UUID userId;
  private Instant lastReadAt;
}
