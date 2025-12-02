package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageCreatedEvent {

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;
}