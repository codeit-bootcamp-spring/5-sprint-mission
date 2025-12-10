package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleUpdatedEvent {

  private final UUID userId;
  private final String oldRole;
  private final String newRole;
}