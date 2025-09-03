package com.sprint.mission.discodeit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

  private final List<T> content;
  private final Object nextCursor;
  private final int size;
  private final boolean hasNext;
  private final Long totalElements;

  public static <T> PageResponse<T> of(List<T> content, Object nextCursor, int size,
      boolean hasNext, Long totalElements) {
    return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
  }

  public <R> PageResponse<R> map(java.util.function.Function<? super T, ? extends R> mapper) {
    List<R> mappedContent = (List<R>) content.stream()
                                             .map(mapper)
                                             .toList();
    return new PageResponse<>(mappedContent, nextCursor, size, hasNext, totalElements);
  }
}