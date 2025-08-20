package com.sprint.mission.discodeit.dto.channel;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> participantIds // private를 생성할 때는 참여하게 되는 사용자의 id가 필요하다
) {}
