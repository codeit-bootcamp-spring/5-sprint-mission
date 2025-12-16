package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BinaryContentUpdatedEvent {
    private final UUID receiverId;
    private final BinaryContentDTO binaryContentDto;
}