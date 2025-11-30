package com.sprint.mission.discodeit.event.message;


import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId
) {
}
