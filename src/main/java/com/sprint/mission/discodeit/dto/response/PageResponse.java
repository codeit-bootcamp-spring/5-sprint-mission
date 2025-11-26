package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Instant nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {
}
