package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "마지막 읽은 시간은 필수입니다")
    @PastOrPresent(message = "마지막 읽은 시간은 과거나 현재여야 합니다")
    Instant newLastReadAt
) {

}
