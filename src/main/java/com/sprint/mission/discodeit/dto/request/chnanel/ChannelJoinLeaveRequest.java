package com.sprint.mission.discodeit.dto.request.chnanel;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelJoinLeaveRequest(
        @NotNull UUID userId
) {
}
