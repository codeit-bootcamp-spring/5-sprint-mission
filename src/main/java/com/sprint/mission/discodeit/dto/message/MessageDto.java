package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(

    UUID id,
    Instant createdAt,
    Instant updatedAt,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    String content,

    UUID channelId,

    UserDto author,

    List<BinaryContentDto> attachments
) {

  public static MessageDto from(Message m, UserDto author, List<BinaryContentDto> attachments) {
    return new MessageDto(
        m.getId(),
        m.getCreatedAt(),
        m.getUpdatedAt(),
        m.getContent(),
        m.getChannelId(),
        author,
        attachments
    );
  }
}
