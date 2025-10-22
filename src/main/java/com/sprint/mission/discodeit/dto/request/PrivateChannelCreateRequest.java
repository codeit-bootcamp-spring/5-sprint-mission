package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "채널에는 사용자가 존재해야 합니다.")
    @Size(min = 1, message = "채널에는 최소 1명 이상의 사용자가 존재해야 합니다.")
    List<UUID> participantIds
) {

}
