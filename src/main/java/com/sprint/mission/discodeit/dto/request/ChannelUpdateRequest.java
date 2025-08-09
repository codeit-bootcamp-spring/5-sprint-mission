package com.sprint.mission.discodeit.dto.request;

import java.util.*;

public record ChannelUpdateRequest(
        UUID channelId,
        String name,
        String description

) {
}
