package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
public class MessageCreateRequest {
    private final String content;
    private final UUID channelId;
    private final UUID userId;
}