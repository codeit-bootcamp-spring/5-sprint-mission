package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.log.LogUtils;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

  public String forLog() {
    return "MessageDto{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", content=" + LogUtils.summarize(content, 30) +
        ", channelId=" + channelId +
        ", authorId=" + author.id() +
        ", attachments" + LogUtils.summarizeAttachments(attachments, 3) +
        "}";
  }
}
