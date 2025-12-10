package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BinaryContentFailEvent {

  UUID binaryContentId;
  Exception exception;
}
