package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "참가자 목록은 비어 있을 수 없습니다.")
    @Size(min = 2, message = "개인 채널은 최소 2명 이상이어야 합니다.")
    List<UUID> participantIds
) {}