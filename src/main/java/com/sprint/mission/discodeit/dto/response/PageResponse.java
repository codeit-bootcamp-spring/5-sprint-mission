package com.sprint.mission.discodeit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {
  public static <T> PageResponse<T> of(
      List<T> content, Object nextCursor, int size, boolean hasNext, Long totalElements) {
    return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
  }
}
