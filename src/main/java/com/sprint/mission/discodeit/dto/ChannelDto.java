package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.log.LogUtils;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserDto> participants,
    Instant lastMessageAt
) {

  public String forLog() {
    return "ChannelDto{" +
        "id=" + id +
        ", type=" + type +
        ", description=" + LogUtils.summarize(description, 30) +
        ", participants=" + (participants != null
        ? participants.stream().map(UserDto::forLog).toList().toString()
        : "[]") +
        ", lastMassageAt=" + lastMessageAt +
        "}";
  }

}
