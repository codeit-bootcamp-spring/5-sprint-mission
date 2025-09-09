package com.sprint.mission.discodeit.dto.response;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cglib.core.internal.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record PageResponse<T>(
    List<T> content,
    int number,
    int size,
    boolean hasNext,
    Long totalElements // null 가능
) {

  public static <T> PageResponse<T> fromSlice(Slice<T> slice) {
    return new PageResponse<>(
        slice.getContent(),
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }

  public static <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }

  public <R> PageResponse<R> map(Function<? super T, ? extends R> mapper) {
    List<R> mapped = new ArrayList<>();
    for (T t : this.content) {
      R r = mapper.apply(t);
      mapped.add(r);
    }
    return new PageResponse<>(mapped, number, size, hasNext, totalElements);
  }
}
