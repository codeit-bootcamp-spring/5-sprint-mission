package com.sprint.mission.discodeit.dto.request;

import java.util.*;

public record MessageUpdateRequest(
        UUID messageId,
        String content
) {
}
