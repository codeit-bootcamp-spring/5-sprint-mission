package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageDto(UUID id, @JsonFormat(shape = JsonFormat.Shape.STRING) Instant createdAt,
                         @JsonFormat(shape = JsonFormat.Shape.STRING) Instant updatedAt,
                         String content, UUID channelId, UserDto author,
                         List<BinaryContentDto> attachments) {

}
