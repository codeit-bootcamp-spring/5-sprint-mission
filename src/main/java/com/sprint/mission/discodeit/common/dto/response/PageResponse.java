package com.sprint.mission.discodeit.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(
    example = """
        {
          "content": [...],
          "nextCursor": "2025-09-04T09:27:55.378176Z",
          "size": 50,
          "hasNext": true,
          "totalElements": 86
        }
        """
)
public record PageResponse<T>(
    List<T> content,
    Instant nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {
}
