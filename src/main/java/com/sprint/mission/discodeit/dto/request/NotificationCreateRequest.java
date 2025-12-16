package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record NotificationCreateRequest(
    UUID receiverId,
    String title,
    String content
) {

}
