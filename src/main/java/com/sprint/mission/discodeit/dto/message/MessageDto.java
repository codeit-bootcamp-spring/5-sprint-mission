package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
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
            m.getChannel().getId(),
            author,
            attachments
        );
    }
}
