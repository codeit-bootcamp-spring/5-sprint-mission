package com.sprint.mission.discodeit.common.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(
    example = """
        {
          "content": [...],
          "nextCursor": "2025-09-04T09:27:55.378176Z",
          "size": 50,
          "hasNext": true
        }
        """
)
public record PaginationResponse<T>(
    List<T> content,
    Instant nextCursor,
    int size,
    boolean hasNext
) {
    public static <T> PaginationResponse<T> empty() {
        return new PaginationResponse<>(List.of(), null, 0, false);
    }
}
