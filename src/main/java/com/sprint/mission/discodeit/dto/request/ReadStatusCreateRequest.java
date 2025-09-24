package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(

    @NotNull(message = "사용자가 존재해야 합니다.")
    UUID userId,

    @NotNull(message = "채널이 존재해야 합니다.")
    UUID channelId,

    @NotNull(message = "가장 최근 읽은 날짜는 존재해야 합니다.")
    @PastOrPresent(message = "가장 최신 읽은 날짜는 과거 혹은 현재 시각으로만 가능합니다.")
    Instant lastReadAt
) {

}
