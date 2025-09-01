package com.sprint.mission.discodeit.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.response.PageResponse;

@Component
public class PageResponseMapper<T> {

	public PageResponse<T> fromSlice(Slice<T> slice) {
		return PageResponse.<T>builder()
		  .content(slice.getContent())
		  .number(slice.getNumber())
		  .size(slice.getSize())
		  .hasNext(slice.hasNext())
		  .totalElements(slice.get().count())
		  .build();
	}

	public PageResponse<T> fromPage(Page<T> page) {
		return PageResponse.<T>builder()
		  .content(page.getContent())
		  .number(page.getNumber())
		  .size(page.getSize())
		  .hasNext(page.hasNext())
		  .totalElements(page.getTotalElements())
		  .build();
	}
}
