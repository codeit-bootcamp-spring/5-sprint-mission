package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotNull(message = "참가자 목록은 필수입니다.")
        @NotEmpty(message = "참가자 목록이 비어있을 수 없습니다.")
        @Size(min = 2, max = 50, message = "참가자는 최소 2명, 최대 50명까지 가능합니다.")
        List<UUID> participantIds
) {

}
