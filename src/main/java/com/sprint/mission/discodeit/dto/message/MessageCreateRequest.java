package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;
import lombok.Data;

@Data
public class MessageCreateRequest {

  private String content;
  private UUID channelId;
  private UUID authorId;

}
