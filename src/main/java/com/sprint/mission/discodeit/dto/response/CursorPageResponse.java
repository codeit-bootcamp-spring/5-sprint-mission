package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;

public record CursorPageResponse<T>(
    List<T> content,
    boolean hasNext,
    Instant lastCursor
) {}


