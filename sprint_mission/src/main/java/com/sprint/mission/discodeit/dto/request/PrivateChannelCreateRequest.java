package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull(message = "참여자가 존재해야 합니다")
    List<UUID> participantIds
) {

}
