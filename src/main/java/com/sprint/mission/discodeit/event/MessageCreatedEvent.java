package com.sprint.mission.discodeit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final UUID authorId;
    private final String authorUsername;
    private final String channelName;
    private final String content;
}