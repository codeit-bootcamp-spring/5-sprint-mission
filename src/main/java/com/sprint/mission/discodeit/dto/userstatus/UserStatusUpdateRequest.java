package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusUpdateRequest {

  private UUID userId;
  private Instant newLastActiveAt;

}
