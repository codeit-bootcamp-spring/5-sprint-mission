package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.log.LogUtils;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    String content,

    @NotNull
    UUID channelId,

    @NotNull
    UUID authorId
) {

  public String forLog() {
    return "MessageCreateRequest{" +
        ", content=" + LogUtils.summarize(content, 30) +
        ", channelId" + channelId +
        ", authorId" + authorId +
        "}";

  }

}
