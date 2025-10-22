package com.sprint.mission.discodeit.dto.neutral;

import com.sprint.mission.discodeit.log.LogUtils;
import java.util.List;
import java.util.UUID;

public record MessageCreateCommand(
    UUID channelId,
    UUID authorId,
    String content,
    List<NewBinaryContent> attachments
) {

  public String forLog() {
    return "MessageCreateCommand{" +
        ", channelId=" + channelId +
        ", authorId=" + authorId +
        ", content=" + LogUtils.summarize(content, 30) +
        ", attachments=" + attachments.stream().map(NewBinaryContent::forLog) +
        "}";
  }

}
