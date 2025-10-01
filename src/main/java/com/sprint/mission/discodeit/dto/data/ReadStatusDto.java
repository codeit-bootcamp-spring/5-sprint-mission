package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(UUID id, UUID userId, UUID channelId,
                            @JsonFormat(shape = JsonFormat.Shape.STRING) Instant lastReadAt) {

}
