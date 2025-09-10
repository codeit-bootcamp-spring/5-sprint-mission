package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String context,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

}
