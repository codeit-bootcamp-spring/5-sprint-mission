package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BinaryContentCreatedEvent {

  private UUID binaryContentId;
  private UUID userId;
  private String fileName;
  private String contentType;
  private byte[] bytes;
}