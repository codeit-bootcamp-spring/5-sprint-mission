package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        @PastOrPresent(message = "수정할 마지막 읽은 시각은 필수입니다.")
        Instant newLastReadAt,
        boolean newNotificationEnabled
) {

}
