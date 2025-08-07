package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateRequest {
    private final String content;
    private final UUID channelId;
    private final UUID authorId;
}
