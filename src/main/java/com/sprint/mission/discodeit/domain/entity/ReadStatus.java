package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ReadStatus extends BaseEntity {
    private boolean read;
    private final UUID userId;
    private final UUID chatRoomId;
}
