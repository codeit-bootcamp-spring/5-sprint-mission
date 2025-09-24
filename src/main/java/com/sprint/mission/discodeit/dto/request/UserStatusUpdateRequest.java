package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull
    @PastOrPresent(message = "lastActiveAt은 과거 혹은 현재로만 선택이 가능합니다.")
    Instant newLastActiveAt
) {

}
