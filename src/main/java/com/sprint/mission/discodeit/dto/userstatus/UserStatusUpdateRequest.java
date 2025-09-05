package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusUpdateRequest {

  private Instant newLastActiveAt;

}
