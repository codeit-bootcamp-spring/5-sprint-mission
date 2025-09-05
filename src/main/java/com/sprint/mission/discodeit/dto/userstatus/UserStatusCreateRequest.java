package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import lombok.Data;

@Data
public class UserStatusCreateRequest {

  private Instant lastReadAt;
}
