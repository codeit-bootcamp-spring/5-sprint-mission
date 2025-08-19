package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreateRequest(

    @NotNull(message = "참여자 ID 목록은 필수입니다.")
    @Size(min = 2, max = 10, message = "비공개 채널은 2명~10명 사이여야 합니다.")
    Set<@NotNull UUID> participantIds
) {

}
