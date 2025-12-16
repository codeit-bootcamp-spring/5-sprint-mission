package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;

public record BinaryContentUpdatedEvent(
    BinaryContentDTO binaryContentDTO
) {

}
