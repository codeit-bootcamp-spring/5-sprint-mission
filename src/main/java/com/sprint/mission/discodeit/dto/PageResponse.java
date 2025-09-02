package com.sprint.mission.discodeit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

  private final List<T> content;
  private final int number;
  private final int size;
  private final Long totalElements;

  public static <T> PageResponse<T> of(org.springframework.data.domain.Slice<T> slice) {
    return new PageResponse<>(
        slice.getContent(),
        slice.getNumber(),
        slice.getSize(),
        null
    );
  }

  public <R> PageResponse<R> map(java.util.function.Function<? super T, ? extends R> mapper) {
    List<R> mappedContent = (List<R>) content.stream()
                                             .map(mapper)
                                             .toList();
    return new PageResponse<>(mappedContent, number, size, totalElements);
  }
}