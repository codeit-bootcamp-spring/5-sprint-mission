package com.sprint.mission.discodeit.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PageResponse<T> {
	private final List<T> content;
	Object nextCursor;
	private final int size;
	private final boolean hasNext;
	private final Long totalElements;
}
