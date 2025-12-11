package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MessageCreatedEvent {
    private final String channelName;
    private final MessageResponse message;
}