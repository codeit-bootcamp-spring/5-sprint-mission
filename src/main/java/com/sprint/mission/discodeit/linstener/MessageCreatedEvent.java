package com.sprint.mission.discodeit.linstener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final UUID senderId;
}
