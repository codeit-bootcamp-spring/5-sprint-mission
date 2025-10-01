package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotEmpty(message = "참여자 ID 목록은 비어 있을 수 없습니다.")
        @Size(min = 2, max = 50, message = "참여자는 최소 2명 이상, 최대 50명까지 가능합니다.")
        List<UUID> participantIds
) {

}
